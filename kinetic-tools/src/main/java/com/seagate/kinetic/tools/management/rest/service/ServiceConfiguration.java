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

import java.io.File;
import java.nio.file.Paths;

/**
 * Service configuration place holder.
 * 
 * @author chiaming
 *
 */
public class ServiceConfiguration {

    // rest home prop name
    public static final String REST_HOME = "kinetic.rest.home";

    private static final String TEMPLET_CONFIG_PATH = "/config";

    private static final String TEMPLET_CONFIG_NAME = "hwview.templet.json";

    private String servletMapping = "/*";

    // service port
    private int port = 8080;

    // https port
    private int httpsPort = 8081;

    public static final String getHardwareConfigTempletPath() {
        return TEMPLET_CONFIG_PATH;
    }

    public static final String getHardwareConfigTempletName() {
        return TEMPLET_CONFIG_NAME;
    }

    public static final String getHardwareConfigTemplet() {
        return getRestHome() + TEMPLET_CONFIG_PATH + File.separator
                + TEMPLET_CONFIG_NAME;
    }

    /**
     * Get rest service home.
     * 
     * @return rest service home.
     */
    public static String getRestHome() {
        String home = null;

        home = System.getProperty(REST_HOME);
        if (home == null) {
            home = Paths.get("").toAbsolutePath().toString();
        }

        return home;
    }

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
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Set https port
     * 
     * @param httpsPort
     *            service port for https rest service
     * 
     */
    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    /**
     * Get https service port.
     * 
     * @return https service port
     */
    public int getHttpsPort() {
        return this.httpsPort;
    }

    /**
     * Get servlet mapping path
     * 
     * @return servlet mapping path
     */
    public String getServletMapping() {
        return this.servletMapping;
    }

    /**
     * Set servlet mapping path.
     * 
     * @param mapping
     *            servlet mapping path
     */
    public void setServletMapping(String mapping) {
        this.servletMapping = mapping;
    }

}
