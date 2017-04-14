/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

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
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class CustomSessionHandler extends AbstractHandler {
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		try {
			HttpSession session = request.getSession();
			if (session.isNew()) {
				out.printf("New Session: %s%n", session.getId());
			} else {
				out.printf("Old Session: %s%n", session.getId());
			}
		} catch (IllegalStateException ex) {
			out.println("Exception!" + ex);
			ex.printStackTrace(out);
		}
		out.close();
	}
}

public class Protium extends AbstractHandler {
	private static Router router;
	private static Logger logger = Logger.getLogger(Protium.class.getName());

	@Override
	public void handle(String target,
	                   Request baseRequest,
	                   HttpServletRequest request,
	                   HttpServletResponse response)
		throws IOException, ServletException {

		HTTPRequestParser parser = new HTTPRequestParser(request);

		HTTPRequest requestData = parser.getHTTPRequest();

		requestData.setURL(target);

		System.err.println(request.getSession().isNew());

		Response responseData;

		try {
			responseData = router.redirect(requestData);
		} catch (NotFoundException e) {
			logger.log(Level.WARNING, "404 Not Found: target " + target);

			baseRequest.setHandled(true);

			response.setContentType("text/html; charset=utf-8");

			String errorPage = new String(
				Files.readAllBytes(
					Paths.get(
						Functions.pathToFile(Constant.DATA_D, "error-404", ".html")
					)));

			errorPage = errorPage.replaceAll("__SERVER_TEXT__", Constant.SERVER_TEXT);
			errorPage = errorPage.replaceAll("__TARGET__", target);

			response.getWriter().println(errorPage);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		baseRequest.setHandled(true);

		response.setContentType(responseData.getContentType());
		response.getWriter().print(responseData.getResponse());
		response.setContentLength(responseData.getResponse().length());
		response.setStatus(responseData.getStatus());
	}

	private static void initialize( ) throws IOException {
		/* Init */

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
	}

	public static void main(String[] args) {

		try {
			initialize();
		} catch (IOException e) {
			System.err.println("Protium failed to initialize!\nHalted.");
			e.printStackTrace();
			System.exit(-1);
		}

		Server server = new Server(8081);

		DefaultSessionIdManager sessionIdManager = new DefaultSessionIdManager(server);
		SessionHandler sessionHandler = new SessionHandler();

		sessionHandler.setHandler(new Protium());


		ContextHandler context = new ContextHandler("/");
		context.setHandler(sessionHandler);

		server.setHandler(context);
		server.setSessionIdManager(sessionIdManager);

		Manager manager = new Manager();
		router = new Router(manager);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			logger.log(Level.OFF, "Jetty failed to start! Halted.", e);
			System.exit(-2);
		}
	}
}
