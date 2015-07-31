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
package com.seagate.kinetic.tools.management.rest.service.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.seagate.kinetic.tools.management.rest.bridge.RestBridgeServiceFactory;
import com.seagate.kinetic.tools.management.rest.message.ErrorResponse;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.service.ServiceContext;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * Generic service handler.
 * 
 * @author chiaming
 *
 */
public abstract class GenericServiceHandler implements ServiceHandler {

    private final Logger logger = Logger.getLogger(ServiceHandler.class
            .getName());

    @Override
    public void service(ServiceContext context) {

        /**
         * get request from context
         */
        HttpServletRequest httpRequest = context.getHttpServletRequest();

        /**
         * rest response message
         */
        RestResponse response = null;

        /**
         * transform request message
         */
        try {

            // transform common http request to rest request
            RestRequest req = HandlerUtil.transformRequest(httpRequest,
                    getRequestMessageClass());

            // transform request specific params
            this.transformRequestParams(httpRequest, req);

            // set request to context
            context.setRequestMessage(req);

            logger.info("received request message: " + req.toJson());

            /**
             * do service
             */
            response = this.doService(context.getRequestMessage());

            logger.info("sending response message: " + response.toJson());

        } catch (Exception e) {

            // construct error response message
            response = new ErrorResponse();

            // set error message
            ((ErrorResponse) response).setErrorMessage(e.getClass().getName()
                    + ":" + e.getMessage());

            logger.log(Level.WARNING, e.getMessage(), e);
        } finally {
            // set response to service context
            context.setResponseMessage(response);
        }
    }

    protected void transformRequestParams(HttpServletRequest httpRequest,
            RestRequest req) {
        ;
    }

    /**
     * perform default service.
     * 
     * @param request
     *            request message
     * 
     * @return response message
     */
    protected RestResponse doService(RestRequest request) {

        RestResponse response = RestBridgeServiceFactory.getServiceInstance()
                .service(request);

        return response;
    }

}
