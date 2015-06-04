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

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.seagate.kinetic.tools.management.rest.message.ErrorResponse;
import com.seagate.kinetic.tools.management.rest.service.ServiceContext;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * Error service handler.
 * 
 * @author chiaming
 *
 * @see ServiceHandler
 */
public class ErrorHandler implements ServiceHandler {

    public static final Logger logger = Logger.getLogger(ErrorHandler.class
            .getName());

    @Override
    public void service(ServiceContext context) {
        ErrorResponse response = new ErrorResponse();

        logger.info("sending error response: " + response.toJson());

        response.setErrorCode(HttpServletResponse.SC_NOT_FOUND);
        response.setErrorMessage("unsupported service.");

        context.setResponseMessage(response);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return null;
    }

}
