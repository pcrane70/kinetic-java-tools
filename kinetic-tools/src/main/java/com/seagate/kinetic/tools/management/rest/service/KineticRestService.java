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

import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;

/**
 * Rest service boot-strap class.
 * 
 * @author chiaming
 *
 */
public class KineticRestService {

    public static final Logger logger = Logger
            .getLogger(KineticRestService.class
            .getName());

    // service configuration
    private ServiceConfiguration config = null;

    private Server server = null;

    /**
     * initialize service.
     * 
     * @param config
     *            service configuration
     * @throws Exception
     */
    public void init(ServiceConfiguration config) throws Exception {

        this.config = config;

        if (config == null) {
            throw new java.lang.NullPointerException("config cannot be null");
        }

        this.server = new Server(config.getPort());

        this.server.setHandler(new RestServiceHandler());
    }

    /**
     * start rest service.
     * 
     * @throws Exception
     *             if any internal error occurred
     */
    public void start() throws Exception {
        this.server.start();

        logger.info("kinetic rest service is ready on port: "
                + config.getPort());
    }

    /**
     * Get service configuration.
     * 
     * @return service configuration
     */
    public ServiceConfiguration getServiceConfiguration() {
        return this.config;
    }


    /**
     * check if service is running
     */
    public boolean isRunning() {

        if (server == null) {
            return false;
        }

        return this.server.isRunning();
    }

    public void stop() throws Exception {
        this.server.stop();
    }

    public void destroy() {
        ;
    }

    /**
     * main class to start Kinetic rest service.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ServiceConfiguration config = new ServiceConfiguration();

        KineticRestService service = new KineticRestService();

        service.init(config);

        service.start();
    }

}
