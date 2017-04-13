/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import net.protium.core.http.HTTPRequest;
import net.protium.core.http.HTTPRequestParser;
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

		HTTPRequest requestData = parser.getRequest();



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

		server.start();
		server.join();
	}
}
