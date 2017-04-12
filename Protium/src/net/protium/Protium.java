/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import net.protium.core.utils.Config;
import net.protium.core.http.HTTPRequestParser;
import net.protium.core.modulemanager.Manager;
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
	@SuppressWarnings("WeakerAccess")
	public static Manager manager;
	private static Logger logger = Logger.getLogger(Protium.class.getName());

	@Override
	public void handle(String target,
	                   Request baseRequest,
	                   HttpServletRequest request,
	                   HttpServletResponse response)
		throws IOException, ServletException {

		HTTPRequestParser parser = new HTTPRequestParser(request);

		net.protium.api.events.Request requestData = parser.getData();

		Config router = new Config("routes");

		if (!router.checkPath(target)) {
			logger.log(Level.WARNING, "asked route " + target + " is not configured.");

			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType("text/plain");
			response.getWriter().print(HttpServletResponse.SC_NOT_FOUND);

			baseRequest.setHandled(true);
			return;
		}

		String module = (String) router.get(Config.toPath(new String[]{ target, "module" }));
		String action = (String) router.get(Config.toPath(new String[]{ target, "action" }));

		net.protium.api.events.Response responseData = manager.getModule(module).onRequest(requestData);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(responseData.getContentType());
		response.getWriter().print(responseData.getResponse());

		baseRequest.setHandled(true);
	}

	public static void main(String[] args) throws Exception {
		try {
			logger.addHandler((new FileHandler(
				Functions.createFile(Constant.LOG_D, Protium.class.getName(), Constant.LOG_EXT))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Server server = new Server(8081);
		server.setHandler(new Protium());

		manager = new Manager();
		manager.loadModule("HelloWorld");

		server.start();
		server.join();
	}
}
