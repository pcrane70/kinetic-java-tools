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

/**
 * Service configuration place holder.
 * 
 * @author chiaming
 *
 */
public class ServiceConfiguration {

    // service port
    private int port = 8080;

    /**
     * Get service port
     * 
     * @return service port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Set service port
     * 
     * @param port
     *            service port for the rest service
     */
    public void serPort(int port) {
        this.port = port;
    }

}
