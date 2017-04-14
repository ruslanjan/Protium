/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import net.protium.api.agents.Config;
import net.protium.api.events.Response;
import net.protium.api.exceptions.NotFoundException;
import net.protium.core.http.HTTPRequest;
import net.protium.core.http.HTTPRequestParser;
import net.protium.core.http.Router;
import net.protium.core.modulemanagement.Manager;
import net.protium.core.utils.Constant;
import net.protium.core.utils.Functions;
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

	private static Manager manager;
	private static Router router;

	private static void initialize( ) throws IOException {

		/* Create necessary dirs */
		String[] paths = { Constant.CONF_D, Constant.DATA_D, Constant.LOG_D, Constant.MOD_D, Constant.RES_D, Constant.ROUTES_D };

		for (String path : paths)
			if (!Files.exists(Paths.get(path))) {
				Files.createDirectory(Paths.get(path));
			}

		/* Initialize logger */
		logger.addHandler(
			(new FileHandler(
				Functions.createFile(Constant.LOG_D, Protium.class.getName(), Constant.LOG_EXT)
			)));

		/* Read configs */
		conf = new Config("server");

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
	}

	private static void _main(String[] args) {

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

		manager = new Manager();
		router = new Router(manager);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			logger.log(Level.OFF, "Jetty failed to start! Halted.", e);
			System.exit(-2);
		}
	}

	public static void main(String[] args) {
		try {
			initialize();
		} catch (IOException e) {
			System.err.println("Protium failed to initialize!\nHalted.");
			e.printStackTrace();
			System.exit(-1);
		}

		_main(args);

		modmgrLastReload = routerLastReload = System.currentTimeMillis();
	}

	private static String getErrorPage(Integer code, String target) {
		String errorPage;
		try {
			errorPage = new String(
				Files.readAllBytes(
					Paths.get(
						Functions.pathToFile(Constant.DATA_D, "error-" + code.toString(), ".html")
					)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failed to load error page '" + Paths.get(
				Functions.createFile(Constant.DATA_D, "error-" + code.toString(), ".html")
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
			logger.log(Level.WARNING, "404 Not Found: target " + target);

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
