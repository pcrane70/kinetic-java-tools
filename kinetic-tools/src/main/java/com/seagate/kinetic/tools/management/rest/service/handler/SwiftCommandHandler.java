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

import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * Swift Command service handler.
 * 
 * @author chiaming
 *
 * @see ServiceHandler
 */
public class SwiftCommandHandler extends ExternalCommandHandler {

    public static final Logger logger = Logger.getLogger(SwiftCommandHandler.class
            .getName());

    public static String SWIFT_REQUEST_CLASS_NAME_PREFIX = EXTERNAL_CLASS_NAME_PREFIX
            + "swift.";
    // swift request super class
    public static String SWIFT_REQUEST = SWIFT_REQUEST_CLASS_NAME_PREFIX
            + "SwiftRequest";

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {

        try {
            // swift request message super class
            return Class.forName(SWIFT_REQUEST);
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected String getExternalClassNamePrefix() {
        return SWIFT_REQUEST_CLASS_NAME_PREFIX;
    }

}
