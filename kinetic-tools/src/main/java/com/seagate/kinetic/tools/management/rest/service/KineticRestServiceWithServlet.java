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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Rest service boot-strap class with servlet service.
 * <p>
 * A self-signed certificate is used for the https service.
 * <p>
 * The clients must trust this self-signed certificate and establish HTTPS
 * connection.
 * <p>
 * For example, the following command uses HTTPS connection to ping the drives.
 * 
 * curl -X POST -k --insecure -v "https://127.0.0.1:8081/ping?discoid=321"
 * <p>
 * 
 * @author chiaming
 *
 */
public class KineticRestServiceWithServlet {

    public static final Logger logger = Logger
            .getLogger(KineticRestServiceWithServlet.class
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
    public void init(ServiceConfiguration config) {

        this.config = config;

        if (config == null) {
            throw new java.lang.NullPointerException("config cannot be null");
        }

        // new server
        server = new Server();

        // set servlet handler
        ServletHandler handler = new ServletHandler();

        // add Servlet
        handler.addServletWithMapping(RestServiceServlet.class,
                config.getServletMapping());

        // set handler
        server.setHandler(handler);

        // http connector
        ServerConnector connector = new ServerConnector(server);
        // set http port
        connector.setPort(config.getPort());

        // https config
        HttpConfiguration https = new HttpConfiguration();

        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();

        sslContextFactory
                .setKeyStorePath(com.seagate.kinetic.simulator.io.provider.nio.ssl.KineticKeyStore.class
                        .getResource(
                        "kinetic.jks").toExternalForm());

        sslContextFactory.setKeyStorePassword("secret");

        sslContextFactory.setKeyManagerPassword("secret");

        // https connector
        ServerConnector sslConnector = new ServerConnector(server,
        new SslConnectionFactory(sslContextFactory, "http/1.1"),
        new HttpConnectionFactory(https));

        sslConnector.setPort(config.getHttpsPort());

        // set http/s connectors
        server.setConnectors(new Connector[] { connector, sslConnector });
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
                + config.getPort() + ", https port: " + config.getHttpsPort()
                + ", mapping=" + config.getServletMapping());
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
     *            http port and https port
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ServiceConfiguration config = new ServiceConfiguration();

        if (args.length > 0) {
            // http port
            int port = Integer.parseInt(args[0]);
            config.setPort(port);
            if (args.length > 1) {
                // https port
                port = Integer.parseInt(args[1]);
                config.setHttpsPort(port);
            }
        }

        // set servlet mapping path
        String mapping = System.getProperty("kinetic.servlet.mapping", "/*");
        config.setServletMapping(mapping);

        KineticRestServiceWithServlet service = new KineticRestServiceWithServlet();

        service.init(config);

        service.start();
    }

}
