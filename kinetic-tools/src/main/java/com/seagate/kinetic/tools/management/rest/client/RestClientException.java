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
package com.seagate.kinetic.tools.management.rest.client;

/**
 * Rest client exception super class.
 * <p>
 * This exception is raised when a kinetic client operation encounter errors.
 * 
 * @author chiaming
 * 
 * @see KineticRestClient
 */
public class RestClientException extends Exception {

    private static final long serialVersionUID = 6342059661150869840L;

    public RestClientException() {
        // TODO Auto-generated constructor stub
    }

    public RestClientException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public RestClientException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public RestClientException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
