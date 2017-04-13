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
import net.protium.core.http.HTTPResponse;
import net.protium.core.http.Router;
import net.protium.core.modulemanagement.Manager;
import net.protium.core.utils.Constant;
import net.protium.core.utils.Functions;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Protium extends AbstractHandler {
	@SuppressWarnings("FieldCanBeLocal")
	private static Manager manager;
	private static Router router;
	private static Logger logger = Logger.getLogger(Protium.class.getName());

	@Override
	public void handle(String target,
	                   Request baseRequest,
	                   HttpServletRequest request,
	                   HttpServletResponse response)
		throws IOException, ServletException {

		HTTPRequestParser parser = new HTTPRequestParser(request);

		HTTPRequest requestData = parser.getRequest();

		HTTPResponse responseData;

		try {
			responseData = (HTTPResponse) router.redirect(requestData);
		} catch (NotFoundException e) {
			logger.log(Level.WARNING, "404 Not Found: URL " + target, e);

			baseRequest.setHandled(true);

			response.setContentType("text/html; charset=utf-8");
			response.getWriter().println("404");

			return;
		}

		baseRequest.setHandled(true);

		response.setContentType(responseData.getContentType());
		response.getWriter().println(responseData.getResponse());
		response.setContentLength(responseData.getResponse().length());
		response.setStatus(responseData.getStatus());
	}

	public static void main(String[] args) throws Exception {
		try {
			logger.addHandler((new FileHandler(Functions.createFile(Constant.LOG_D, Protium.class.getName(), Constant.LOG_EXT))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Server server = new Server(8081);
		server.setHandler(new Protium());

		manager = new Manager();
		router = new Router(manager);
		server.start();
		server.join();
	}
}
