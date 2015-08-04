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

import com.seagate.kinetic.tools.management.rest.message.Constants;
import com.seagate.kinetic.tools.management.rest.message.ErrorResponse;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.service.handler.HandlerMap;

/**
 * Handle HTTP rest request.
 * 
 * @author chiaming
 *
 */
public class HttpService {

    /**
     * Handle rest request/respond.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public static void handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        // get service handler
        ServiceHandler handler = getServiceHandler(request);

        // construct service context
        ServiceContext context = new ServiceContext(request);

        // handler request
        handler.service(context);

        // get response message
        RestResponse resp = context.getResponseMessage();

        // get response message as string
        String body = resp.toString();

        MessageType mtype = resp.getMessageType();
        if (MessageType.EXTERNAL_REPLY == mtype) {
            /**
             * for external service message, the response content is set to
             * plain text even though the format could be Json.
             */
            response.setContentType(Constants.PLAIN_CONTENT_TYPE);
        } else {
            /**
             * For Kinetic service, the content is in Json format.
             */
            response.setContentType(Constants.JSON_CONTENT_TYPE);
        }

        // set http response status code
        if (resp instanceof ErrorResponse) {
            response.setStatus(((ErrorResponse) resp).getErrorCode());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        // write body
        response.getWriter().println(body);
    }

    /**
     * Get service handler based on the request message
     * 
     * @param request
     *            http rest message
     * 
     * @return the corresponding service handler to handle the request message.
     */
    private static ServiceHandler getServiceHandler(HttpServletRequest request) {

        // get request URI
        String path = request.getRequestURI();

        // get handler based on the request uri
        ServiceHandler handler = HandlerMap.findHandler(path);

        // service handler
        return handler;
    }

}
