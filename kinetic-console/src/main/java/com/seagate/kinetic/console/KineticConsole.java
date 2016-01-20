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
package com.seagate.kinetic.console;

import java.io.File;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.seagate.kinetic.console.servlet.ConsoleServlet;
import com.seagate.kinetic.tools.management.rest.service.RestServiceServlet;

public class KineticConsole {
    private final static Logger logger = Logger.getLogger(KineticConsole.class
            .getName());
    private ConsoleConfiguration config;
    private Server server = null;

    /**
     * initialize service.
     * 
     * @param config
     *            service configuration
     * @throws Exception
     */
    public void init(ConsoleConfiguration config) {

        this.config = config;

        if (config == null) {
            throw new java.lang.NullPointerException("config cannot be null");
        }

        // new server
        server = new Server();

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(config.getWelcomeFiles());
        resourceHandler.setResourceBase(ConsoleConfiguration.getConsoleHome()
                + File.separator + config.getResourceBase());

        ServletContextHandler contextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        contextHandler.setContextPath(config.getContextPath());
        contextHandler.setResourceBase(config.getResourceBase());
        ServletHolder holder = contextHandler.addServlet(ConsoleServlet.class,
                "/servlet/*");

        holder.setInitOrder(0);
        holder.setInitParameter("resourceBase", "/servlet");
        holder.setInitParameter("pathInfoOnly", "true");
        holder.setInitParameter("unavailableThreshold",
                "" + config.getUnavailableThreshold());
        holder.setDisplayName("ConsoleServlet");

        holder = contextHandler.addServlet(RestServiceServlet.class,
                "/kinetic/*");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, contextHandler,
                new DefaultHandler() });
        server.setHandler(handlers);

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
                        .getResource("kinetic.jks").toExternalForm());

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
                + config.getPort() + ", https port: " + config.getHttpsPort());
    }

    public static void main(String[] args) throws Exception {
        ConsoleConfiguration consoleConfiguration = new ConsoleConfiguration();
        KineticConsole console = new KineticConsole();
        
        if (args.length > 0) {
            // http port
            int port = Integer.parseInt(args[0]);
            consoleConfiguration.setPort(port);
            if (args.length > 1) {
                // https port
                port = Integer.parseInt(args[1]);
                consoleConfiguration.setHttpsPort(port);
            }
        }

        console.init(consoleConfiguration);
        console.start();
    }
}
