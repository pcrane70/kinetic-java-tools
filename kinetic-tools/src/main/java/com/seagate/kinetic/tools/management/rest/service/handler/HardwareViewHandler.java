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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareView;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareViewRequest;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareViewResponse;
import com.seagate.kinetic.tools.management.rest.service.ServiceConfiguration;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

public class HardwareViewHandler extends GenericServiceHandler implements
        ServiceHandler {

    private static final Logger logger = Logger
            .getLogger(HardwareViewHandler.class.getName());

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return HardwareViewRequest.class;
    }

    @Override
    protected void transformRequestParams(HttpServletRequest httpRequest,
            RestRequest req) {

        if (httpRequest.getContentLength() <= 0) {

            HardwareViewRequest request = (HardwareViewRequest) req;

            Map<String, String[]> params = httpRequest.getParameterMap();

            // get name of config file
            String[] names = params.get("name");
            if (names != null) {
                request.setName(names[0]);
            }
        }
    }

    /**
     * Prototype: return a constant hardware view
     */
    @Override
    protected RestResponse doService(RestRequest req) {

        HardwareViewRequest request = (HardwareViewRequest) req;

        HardwareViewResponse response = new HardwareViewResponse();

        HardwareView view = readConfig(request);

        if (view == null) {
            view = readTemplet();
        }

        response.setHardwareView(view);

        return response;
    }

    private HardwareView readConfig(HardwareViewRequest request) {

        String path = ServiceConfiguration.getRestHome()
                + ServiceConfiguration.getHardwareConfigTempletPath()
                + File.separator
                + request.getName();

        logger.info("config path=" + path);

        HardwareView view = this.doRead(path);

        return view;
    }

    private HardwareView readTemplet() {

        String path = ServiceConfiguration.getHardwareConfigTemplet();

        HardwareView view = this.doRead(path);

        logger.info("templet path=" + path);

        return view;
    }

    private HardwareView doRead(String path) {

        HardwareView view = null;

        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            view = gson.fromJson(br, HardwareView.class);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        return view;
    }

}
