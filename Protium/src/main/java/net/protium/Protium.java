/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import groovy.json.JsonException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.protium.api.agents.Config;
import net.protium.api.exceptions.FileReadException;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.http.Response;
import net.protium.api.utils.Constant;
import net.protium.api.utils.Functions;
import net.protium.core.console.BasicCommandList;
import net.protium.core.console.JConsole;
import net.protium.core.gui.GUIThread;
import net.protium.core.http.HTTPRequest;
import net.protium.core.http.HTTPRequestParser;
import net.protium.core.http.Router;
import net.protium.core.modules.management.Manager;
import net.protium.core.util.Reloader;
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
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Protium extends AbstractHandler {

	private static final Logger logger = Logger.getLogger(Protium.class.getName());
	public static Manager manager;
	private static long routerLastReload;
	private static long modmgrLastReload;
	private static String profilePath;
	private static Config conf;
	private static Router router;

	private static void initialize( ) {

		//region Create necessary dirs
		String[] paths = { Constant.CONF_DIR, Constant.DATA_DIR, Constant.LOG_DIR, Constant.MOD_DIR, Constant.RES_DIR, Constant.ROUTES_DIR, Constant.SCHEMA_DIR };

		for (String path : paths)
			if (!Files.exists(Paths.get(path))) {
				try {
					Files.createDirectory(Paths.get(path));
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Failed to create working folder '" + path + "'", e);
					System.exit(-4);
				}
			}
		//endregion

		//region Initialize logger
		try {
			logger.addHandler(
				(new FileHandler(
					Functions.createFile(Constant.LOG_DIR, Protium.class.getSimpleName(), Constant.LOG_EXT)
				)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to initalize logger.", e);
		}
		//endregion

		//region Unpack JSON Schemas
		try {
			JarFile jarFile = new JarFile(new File(Protium.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));

			Enumeration < JarEntry > entries = jarFile.entries();

			for (JarEntry jarEntry = entries.nextElement(); entries.hasMoreElements(); jarEntry = entries.nextElement()) {
				if (
					(jarEntry.getName().startsWith("schemas/") ||
						jarEntry.getName().startsWith("/schemas/")) &&
						(jarEntry.getName().endsWith(Constant.SCHEMA_EXT))
					) {

					Path filePath = Paths.get(Functions.createFile(
						Constant.SCHEMA_DIR,
						jarEntry.getName().split("[/.]")[1],
						Constant.SCHEMA_EXT));
					if (Files.exists(filePath))
						continue;
					Files.copy(
						jarFile.getInputStream(jarEntry),
						filePath);
				}
			}
		} catch (IOException | URISyntaxException e) {
			logger.log(Level.SEVERE, "Failed to copy JSON schemas from JAR! Please, download and install them manually.", e);
		}
		//endregion

		//region Read configs
		try {
			conf = new Config("server", "server");
		} catch (IOException | FileReadException | JsonException e) {
			logger.log(Level.SEVERE, "Failed to read 'server' config.", e);
			System.exit(-3);
		}

		String profile = conf.checkPath("profile") ? conf.getString("profile") : "default";

		profilePath = Config.toPath(new String[]{ "profiles", profile });

		Reloader.ROUTER_RELOAD_INTERVAL = conf.checkPath(
			Config.toPath(new String[]{ profilePath, "router.reloadInterval" })
		) ? conf.getInteger(
			Config.toPath(new String[]{ profilePath, "router.reloadInterval" })
		) : Reloader.ROUTER_RELOAD_INTERVAL;

		Reloader.MANAGER_RELOAD_INTERVAL = conf.checkPath(
			Config.toPath(new String[]{ profilePath, "moduleManager.reloadInterval" })
		) ? conf.getInteger(
			Config.toPath(new String[]{ profilePath, "moduleManager.reloadInterval" })
		) : Reloader.MANAGER_RELOAD_INTERVAL;
		// endregion

		//region Initialize core agents
		manager = new Manager();
		manager.enableAll();
		router = new Router(manager);
		//endregion

		//region Start Reloader
		Reloader reloader = new Reloader(router, manager);
		(new Thread(reloader)).start();
		//endregion

		//region Start UI agents
		runGUI();

		try {
			runConsole();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		//endregion
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
		context.setInitParameter("org.eclipse.jetty.servlets.CrossOriginFilter", "/*");

		server.setHandler(context);
		server.setSessionIdManager(sessionIdManager);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Jetty failed to start! Halted.", e);
			System.exit(-2);
		}
	}

	public static void main(String[] args) {
		//region Parse command-line options
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
		//endregion

		initialize();
		_main();
	}

	private static void runConsole( ) throws NoSuchMethodException {


		BasicCommandList commandList = new BasicCommandList();

		commandList.register("modctl", (Object[] args) -> {

			if (args.length < 1)
				return "modctl: missing operation!";

			switch ((String) args[0]) {
				case "reload":
					manager.reloadModules();
					return "Success!";
			}

			if (args.length < 2)
				return "modctl: missing module name!";

			switch ((String) args[0]) {
				case "status":
					return "Module status: " + manager.getExtendedStatus((String) args[1]);
				case "enable":
					try {
						manager.enableModule((String) args[1]);
						return "modctl: success";
					} catch (NotFoundException e) {
						return "modctl: fail\n\tModuleManager: " + e.getMessage();
					}
				case "disable":
					try {
						manager.disableModule((String) args[1]);
						return "modctl: success";
					} catch (NotFoundException e) {
						return "modctl: fail\n\tModuleManager: " + e.getMessage();
					}
				default:
					return "modctl: cannot find command!";
			}
		});

		commandList.register("clear", (args) -> {
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				try {
					Runtime.getRuntime().exec("cmd /c cls");
				} catch (IOException ignored) {
				}
			} else {
				System.out.print("\\033[H\\033[2J");
			}

			System.out.flush();
			return "";
		});

		commandList.register("control", (args) -> {
			Protium.runGUI();
			return null;
		});

		commandList.register("exit", (args) -> {
			System.exit(0);
			return "";
		});

		JConsole console = new JConsole(commandList);
		(new Thread(console)).start();
	}

	public static void runGUI( ) {
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

	private static void changeWorkingDir(String dir) {
		File file = new File(dir);
		Constant.HOME_DIR = file.getAbsolutePath();

		//region CWD
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
		//endregion
	}

	@Override
	public void handle(String target,
	                   Request baseRequest,
	                   HttpServletRequest request,
	                   HttpServletResponse response)
		throws IOException, ServletException {

		HTTPRequestParser parser = new HTTPRequestParser(request);

		HTTPRequest requestData = parser.getHTTPRequest();

		HttpSession session = request.getSession();

		requestData.setURL(target);
		requestData.setSession(session);

		Response responseData;

		//region Perform request processor
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
		//endregion

		//region Show error page if needed
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
		//endregion

		//region Set response fields
		response.setStatus(responseData.getStatus());
		response.setContentType(responseData.getContentType());
		response.getWriter().print(responseData.getResponse());
		response.setContentLength(responseData.getResponse().length());

		Map headers = responseData.getHeaders();

		if (conf.checkPath(Config.toPath(
			new String[]{ profilePath, "http.customHeaders" })
		)) {
			headers.putAll(
				conf.getMap(Config.toPath(
					new String[]{ profilePath, "http.customHeaders" })
				));
		}

		headers.forEach((key, value) -> {

			if ((value instanceof Integer)) {
				response.addIntHeader((String) key, (Integer) value);
			} else if ((value instanceof Long)) {
				response.addDateHeader((String) key, (Long) value);
			} else {
				String data;

				try {
					data = value.toString();
				} catch (Exception ignored) {
					try {
						data = (String) value;
					} catch (Exception ignored1) {
						data = null;
					}
				}

				if (data != null) {
					response.addHeader((String) key, data);
				}
			}

		});
		//endregion

		response.getHeaderNames().forEach((value) ->
			System.out.println(value + " = " + response.getHeader(value)));

		baseRequest.setHandled(true);
	}
}
