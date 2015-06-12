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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.seagate.kinetic.tools.management.rest.message.ErrorResponse;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.service.handler.HandlerMap;

/**
 * kinetic tool rest service handler.
 * 
 * @author chiaming
 *
 */
public class RestServiceHandler extends AbstractHandler {

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

        // set http response status code
        if (resp instanceof ErrorResponse) {
            response.setStatus(((ErrorResponse) resp).getErrorCode());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        // write body
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

}
