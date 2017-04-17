/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.protium.api.agents.Config;
import net.protium.api.events.Response;
import net.protium.api.exceptions.FileReadException;
import net.protium.api.exceptions.NotFoundException;
import net.protium.core.cli.Console;
import net.protium.core.gui.GUIThread;
import net.protium.core.http.HTTPRequest;
import net.protium.core.http.HTTPRequestParser;
import net.protium.core.http.Router;
import net.protium.core.modules.management.Manager;
import net.protium.api.utils.Constant;
import net.protium.api.agents.Functions;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Protium extends AbstractHandler {

	private static long ROUTER_RELOAD_TIMEOUT = 10 * 1000; // 10 seconds default.
	private static long MODMGR_RELOAD_TIMEOUT = 0; // Disabled by default.

	private static long routerLastReload;
	private static long modmgrLastReload;

	private static String profilePath;

	private static Logger logger = Logger.getLogger(Protium.class.getName());

	private static Config conf;

	public static Manager manager;
	private static Router router;

	private static void initialize( ) {

		/* Create necessary dirs */
		String[] paths = { Constant.CONF_DIR, Constant.DATA_DIR, Constant.LOG_DIR, Constant.MOD_DIR, Constant.RES_DIR, Constant.ROUTES_DIR };

		for (String path : paths)
			if (!Files.exists(Paths.get(path))) {
				try {
					Files.createDirectory(Paths.get(path));
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Failed to create working folder '" + path + "'", e);
					System.exit(-4);
				}
			}

		/* Initialize logger */
		try {
			logger.addHandler(
				(new FileHandler(
					Functions.createFile(Constant.LOG_DIR, Protium.class.getName(), Constant.LOG_EXT)
				)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to initalize logger.", e);
		}

		/* Read configs */
		try {
			conf = new Config("server");
		} catch (IOException | FileReadException e) {
			logger.log(Level.OFF, "Failed to read 'server' config.", e);
			System.exit(-3);
		}

		String profile = conf.checkPath("profile") ? conf.getString("profile") : "default";

		profilePath = Config.toPath(new String[]{ "profiles", profile });

		if (!conf.checkPath(
			profilePath
		)) {
			logger.severe("ERROR: couldn't read server profile 'server'/" + profilePath);
		}

		ROUTER_RELOAD_TIMEOUT = conf.checkPath(
			Config.toPath(new String[]{ profilePath, "router.reloadInterval" })
		) ? conf.getInteger(
			Config.toPath(new String[]{ profilePath, "router.reloadInterval" })
		) : ROUTER_RELOAD_TIMEOUT;

		MODMGR_RELOAD_TIMEOUT = conf.checkPath(
			Config.toPath(new String[]{ profilePath, "moduleManager.reloadInterval" })
		) ? conf.getInteger(
			Config.toPath(new String[]{ profilePath, "moduleManager.reloadInterval" })
		) : MODMGR_RELOAD_TIMEOUT;

		manager = new Manager();
		router = new Router(manager);
	}

	private static void _main( ) {

		Server server = new Server();

		ServerConnector connector = new ServerConnector(server);

		if (conf.checkPath(
			Config.toPath(new String[]{ profilePath, "http.port" })
		)) {
			connector.setPort(conf.getInteger(
				Config.toPath(new String[]{ profilePath, "http.port" })
			));
		} else {
			connector.setPort(80);
		}

		connector.setHost(conf.getString(
			Config.toPath(new String[]{ profilePath, "http.host" })
		));

		if (conf.checkPath(
			Config.toPath(new String[]{ profilePath, "http.idleTimeout" })
		)) {
			connector.setIdleTimeout(conf.getInteger(
				Config.toPath(new String[]{ profilePath, "http.idleTimeout" })
			));
		} else {
			connector.setIdleTimeout(30000);
		}

		server.addConnector(connector);

		DefaultSessionIdManager sessionIdManager = new DefaultSessionIdManager(server);
		SessionHandler sessionHandler = new SessionHandler();

		sessionHandler.setHandler(new Protium());


		ContextHandler context = new ContextHandler("/");
		context.setHandler(sessionHandler);

		server.setHandler(context);
		server.setSessionIdManager(sessionIdManager);

		try {
			server.start();
			//server.join();
		} catch (Exception e) {
			logger.log(Level.OFF, "Jetty failed to start! Halted.", e);
			System.exit(-2);
		}
	}

	public static void main(String[] args) {

		OptionParser parser = new OptionParser();

		parser
			.accepts("home-dir", "Specifies a directory to store all required files.")
			.withRequiredArg();

		parser
			.accepts("d", "Shorthand for --home-dir option. If both are specified, --home-dir is preferred. ")
			.withRequiredArg();

		parser
			.accepts("?", "Shorthand for --help option.");

		parser
			.accepts("help", "Show this help.");

		OptionSet options = parser.parse(args);

		if (options.has("?") || options.has("help")) {
			try {
				parser.printHelpOn(System.err);
			} catch (IOException ignored) {
			}

			System.exit(0);
		}

		if (options.has("home-dir")) {
			changeWorkingDir((String) options.valueOf("home-dir"));
		} else if (options.has("h")) {
			changeWorkingDir((String) options.valueOf("h"));
		}

		initialize();

		_main();

		runGUI();

		Console console = new Console();

		try {
			console.start();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		//modmgrLastReload = routerLastReload = System.currentTimeMillis();
	}

	private static void runGUI() {
		Thread guiThread = new Thread(new GUIThread());
		guiThread.start();
	}


	private static String getErrorPage(Integer code, String target) {
		String errorPage;
		try {
			errorPage = new String(
				Files.readAllBytes(
					Paths.get(
						Functions.pathToFile(Constant.DATA_DIR, "error-" + code.toString(), ".html")
					)));

		} catch (IOException e) {
			logger.log(Level.SEVERE, "failed to load error page '" + Paths.get(
				Functions.createFile(Constant.DATA_DIR, "error-" + code.toString(), ".html")
			) + "'", e);
			return String.valueOf(code);
		}

		errorPage = errorPage.replaceAll("__SERVER_TEXT__", Constant.SERVER_TEXT);
		errorPage = errorPage.replaceAll("__TARGET__", target);
		return errorPage;
	}

	private static void reloadIfNeeded( ) {
		long currentTime = System.currentTimeMillis();

		if (currentTime - routerLastReload > ROUTER_RELOAD_TIMEOUT && ROUTER_RELOAD_TIMEOUT > 0) {
			routerLastReload = currentTime;
			logger.info("Reloading Router");
			router.reload();
		}

		currentTime = System.currentTimeMillis();

		if (currentTime - modmgrLastReload > MODMGR_RELOAD_TIMEOUT && MODMGR_RELOAD_TIMEOUT > 0) {
			modmgrLastReload = currentTime;
			logger.info("Reloading ModuleManager");
			manager.reloadModules();
		}

	}

	private static void changeWorkingDir(String dir) {
		File file = new File(dir);
		Constant.HOME_DIR = file.getAbsolutePath();

		Constant.RES_DIR = Functions.implode(
			new String[]{ Constant.HOME_DIR, Constant.RES_DIR }, File.separator);
		Constant.CONF_DIR = Functions.implode(
			new String[]{ Constant.HOME_DIR, Constant.CONF_DIR }, File.separator);
		Constant.ROUTES_DIR = Functions.implode(
			new String[]{ Constant.HOME_DIR, Constant.ROUTES_DIR }, File.separator);
		Constant.LOG_DIR = Functions.implode(
			new String[]{ Constant.HOME_DIR, Constant.LOG_DIR }, File.separator);
		Constant.DATA_DIR = Functions.implode(
			new String[]{ Constant.HOME_DIR, Constant.DATA_DIR }, File.separator);
		Constant.MOD_DIR = Functions.implode(
			new String[]{ Constant.HOME_DIR, Constant.MOD_DIR }, File.separator);

	}

	@Override
	public void handle(String target,
	                   Request baseRequest,
	                   HttpServletRequest request,
	                   HttpServletResponse response)
		throws IOException, ServletException {

		reloadIfNeeded();

		HTTPRequestParser parser = new HTTPRequestParser(request);

		HTTPRequest requestData = parser.getHTTPRequest();

		requestData.setURL(target);

		Response responseData;

		try {
			responseData = router.perform(requestData);
		} catch (NotFoundException e) {
			logger.log(Level.WARNING, "404 Not Found: target " + target, e);

			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType(Constant.HTML_CONTENT_TYPE);

			response.getWriter().println(getErrorPage(404, target));

			baseRequest.setHandled(true);
			return;
		}

		if (
			(responseData.getResponse() == null || responseData.getResponse().length() < 1)
				&& (responseData.getStatus() / 100) > 3 // All status codes that >= 400 are error codes
			) {

			response.setStatus(responseData.getStatus());
			response.setContentType(Constant.HTML_CONTENT_TYPE);

			String errorPage = getErrorPage(responseData.getStatus(), target);

			response.getWriter().print(errorPage);

			baseRequest.setHandled(true);
			return;
		}

		response.setStatus(responseData.getStatus());
		response.setContentType(responseData.getContentType());
		response.getWriter().print(responseData.getResponse());
		response.setContentLength(responseData.getResponse().length());

		baseRequest.setHandled(true);
	}
}
