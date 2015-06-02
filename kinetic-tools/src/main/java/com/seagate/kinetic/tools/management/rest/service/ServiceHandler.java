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

import com.seagate.kinetic.tools.management.rest.message.RestRequest;


/**
 * All request service handlers must implement this interface.
 * 
 * @author chiaming
 *
 */
public interface ServiceHandler {

    /**
     * Each service handler must implement this interface.
     * <p>
     * The service handler obtains the request data from the service context.
     * 
     * @param context
     *            request service context
     */
    public void service(ServiceContext context);

    /**
     * Get request message class instance.
     * 
     * @return request message class instance for this handler.
     * 
     * @see RestRequest
     */
    @SuppressWarnings("rawtypes")
    public Class getRequestMessageClass();
}
