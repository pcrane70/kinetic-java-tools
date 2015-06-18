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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

/**
 * 
 * Service handler utility.
 * 
 * @author chiaming
 *
 */
public class HandlerUtil {

    private static final Logger logger = Logger.getLogger(HandlerUtil.class
            .getName());

    /**
     * Transfrom common http request params to rest request message. This is
     * used for simple request that contains one device only. Use json body for
     * request if contains more than one device.
     * 
     * @param httpRequest
     *            http request message
     * @param clazz
     *            rest request class instance
     * @return Rest request
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("rawtypes")
    public static RestRequest paramsToRequest(HttpServletRequest httpRequest,
            Class clazz) throws InstantiationException, IllegalAccessException {

        // instantiate object
        RestRequest req = (RestRequest) clazz.newInstance();

        /**
         * get from request params if present
         */
        Map<String, String[]> params = httpRequest.getParameterMap();

        // check if discoId is present
        String[] discoId = params.get("discoid");
        if (discoId != null) {
            req.setDiscoId(discoId[0]);
        } else {

            // list of devices for the request
            List<DeviceId> devices = new ArrayList<DeviceId>();

            // ip param name
            String[] ips = params.get("ip");

            if (ips != null) {

                /**
                 * device holder
                 */
                DeviceId dev = new DeviceId();

                /**
                 * set device Id
                 */
                dev.setIps(ips);

                /**
                 * set ports
                 */
                String[] ports = params.get("port");
                if (ports != null) {
                    dev.setPort(Integer.parseInt(ports[0]));
                }

                /**
                 * set tls port
                 */
                ports = params.get("tlsport");
                if (ports != null) {
                    dev.setTlsPort(Integer.parseInt(ports[0]));
                }

                /**
                 * set wwn
                 */
                String[] wwns = params.get("wwn");
                if (wwns != null) {
                    dev.setWwn(wwns[0]);
                }

                devices.add(dev);

                req.setDevices(devices);
            }
        }

        /**
         * set identity
         */
        String[] ids = params.get("identity");
        if (ids != null) {
            req.setIdentity(ids[0]);
        }

        /**
         * set key
         */
        String[] keys = params.get("key");
        if (keys != null) {
            req.setKey(keys[0]);
        }

        /**
         * cluster version
         */
        String[] clversion = params.get("clversion");
        if (clversion != null) {
            req.setClversion(Integer.parseInt(clversion[0]));
        }

        String[] useSsl = params.get("usessl");
        if (useSsl != null) {
            req.setUseSsl(Boolean.parseBoolean(useSsl[0]));
        }

        String[] reqTimeout = params.get("reqtimeout");
        if (reqTimeout != null) {
            req.setRequestTimeout(Integer.parseInt(reqTimeout[0]));
        }

        return req;
    }

    @SuppressWarnings("rawtypes")
    public static RestRequest jsonBodyToRequest(HttpServletRequest httpRequest,
            Class clazz) {

        RestRequest restRequest = null;

        // Read from request body
        try {

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = httpRequest.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String data = buffer.toString();

            restRequest = (RestRequest) MessageUtil.fromJson(data, clazz);

        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        }

        return restRequest;
    }

    @SuppressWarnings("rawtypes")
    public static RestRequest transformRequest(HttpServletRequest httpRequest,
            Class clazz) throws InstantiationException, IllegalAccessException {

        RestRequest req = null;

        int clen = httpRequest.getContentLength();

        if (clen <= 0) {
            req = paramsToRequest(httpRequest, clazz);
        } else {
            req = jsonBodyToRequest(httpRequest, clazz);
        }

        return req;
    }

}
