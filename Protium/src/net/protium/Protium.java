/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

//import net.protium.core.modulemanager.Manager;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Protium extends AbstractHandler {
    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {

        InputStream input = request.getInputStream();
        assert input != null;
        byte[] buffer = new byte[1024];
        StringBuilder rawInput = new StringBuilder("");
        while (true) {
            int gotBytes = input.read(buffer);
            if(gotBytes <= 0)
                break;
            rawInput.append(new String(buffer));
        }

        System.out.println("POST data: " + rawInput.toString());

        response.setContentType("text/html; charset=utf-8");

        response.setStatus(200);

        response.getWriter().println(rawInput.toString());

        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);
        server.setHandler(new Protium());

        server.start();
        server.join();
    }
}