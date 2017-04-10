/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import net.protium.api.config.ConfigReader;
import net.protium.core.http.HTTPRequest;
import net.protium.core.http.HTTPRequestParser;
import net.protium.core.http.HTTPResponse;
import net.protium.core.modulemanager.Manager;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Protium extends AbstractHandler {
	@SuppressWarnings("WeakerAccess")
	public static Manager manager;
    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {

		HTTPRequestParser parser = new HTTPRequestParser(request);

		HTTPRequest requestData = parser.getData();


	    ConfigReader router = new ConfigReader("routes");

	    String module = (String) router.get(ConfigReader.toPath(new String[]{target, "module"}));
	    String action = (String) router.get(ConfigReader.toPath(new String[]{target, "action"}));
	    requestData.setAction(action);

	    HTTPResponse responseData = (HTTPResponse) manager.getModule(module).onRequest(requestData);

	    response.setContentType(responseData.getContentType());
	    response.getWriter().print(responseData.getResponse());

        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);
        server.setHandler(new Protium());

        manager = new Manager();

        server.start();
        server.join();
    }
}
