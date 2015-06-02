/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.rest.service;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.service.handler.HandlerMap;

/**
 * Rest service boot-strap class.
 * 
 * @author chiaming
 *
 */
public class KineticRestService extends AbstractHandler {

    public static final Logger logger = Logger
            .getLogger(KineticRestService.class
            .getName());

    /**
     * Rest request routing engine.
     */
    @Override
    public void handle(String target, Request baseRequest,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // get service handler
        ServiceHandler handler = this.getServiceHandler(request);

        // construct service context
        ServiceContext context = new ServiceContext(request);

        // handler request
        handler.service(context);

        // get response message
        RestResponse resp = context.getResponseMessage();

        // convert to json
        String body = resp.toJson();

        // write response message
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(body);

        // set message processed flag
        baseRequest.setHandled(true);
    }

    /**
     * Get service handler based on the request message
     * 
     * @param request
     *            http rest message
     * 
     * @return the corresponding service handler to handle the request message.
     */
    private ServiceHandler getServiceHandler(HttpServletRequest request) {

        // get request URI
        String path = request.getRequestURI();

        // get handler based on the request uri
        ServiceHandler handler = HandlerMap.findHandler(path);

        // service handler
        return handler;
    }

    /**
     * main class to start Kinetic rest service.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // LogManager.getLogManager().reset();

        int port = 8080;

        /**
         * override if port is specified as the first argument.
         */
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        // construct new instance of server
        Server server = new Server(port);
        // set message handler
        server.setHandler(new KineticRestService());

        // start server
        server.start();

        logger.info("kinetic rest service is ready on port: " + port);

        server.join();
    }

}
