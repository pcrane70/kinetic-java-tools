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
package com.seagate.kinetic.tools.management.rest.bridge.impl;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.bridge.RestBridgeService;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.ping.PingResponse;

/**
 * 
 * This is an example of a dummy RestBridgeService that "echo" a Ping response
 * with the requested data .
 * 
 * @author chiaming
 *
 */
public class SampleRestBridgeService implements RestBridgeService {

    public SampleRestBridgeService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public RestResponse service(RestRequest request) {

        // get message type
        MessageType mtype = request.getMessageType();

        // response message
        RestResponse response = null;

        // example ping service implementation
        if (mtype == MessageType.PING) {

            System.out.println("received request message: " + request.toJson());

            // construct ping response
            response = new PingResponse();

            // do ping service, below is an example

            // set response data
            List<DeviceStatus> respDevices = new ArrayList<DeviceStatus>();

            List<DeviceId> reqDevices = request.getDevices();

            for (DeviceId id : reqDevices) {

                DeviceId device = new DeviceId();

                device.setIps(id.getIps());
                device.setPort(id.getPort());
                device.setTlsPort(id.getTlsPort());

                DeviceStatus dstatus = new DeviceStatus();
                dstatus.setDevice(device);

                device.setWwn(id.getWwn());

                respDevices.add(dstatus);
            }

            ((PingResponse) response).setDevices(respDevices);
        }

        return response;
    }

}
