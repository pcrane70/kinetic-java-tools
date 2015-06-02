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

import kinetic.admin.Capacity;
import kinetic.admin.Limits;

import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;
import com.seagate.kinetic.tools.management.rest.bridge.RestBridgeService;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceInfo;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.ErrorResponse;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverResponse;
import com.seagate.kinetic.tools.management.rest.message.getlog.DeviceLog;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogResponse;
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

        switch (mtype) {
        case PING:
            response = ping(request);
            break;
        case DISCOVER:
            response = discover(request);
            break;
        case GETLOG:
            response = getlog(request);
            break;
        default:
            response = new ErrorResponse();
        }

        return response;
    }

    private RestResponse getlog(RestRequest request) {

        GetLogResponse resp = new GetLogResponse();

        List<DeviceLog> listOfLogs = new ArrayList<DeviceLog>();

        for (int i = 0; i < 2; i++) {
            DeviceLog dlog = new DeviceLog();

            Capacity c = new Capacity();
            c.setPortionFull(0.2f);

            dlog.setCapacity(c);

            Limits limits = new Limits();
            limits.setMaxConnections(100);
            limits.setMaxKeySize(4096);
            dlog.setLimits(limits);

            DeviceStatus status = new DeviceStatus();

            DeviceId did = new DeviceId();
            did.setPort(8123 + i);
            did.setTlsPort(8443 + i);

            status.setDevice(did);
            dlog.setDeviceStatus(status);

            listOfLogs.add(dlog);

            resp.setDeviceLogs(listOfLogs);
        }

        return resp;
    }

    private RestResponse discover(RestRequest request) {

        List<DeviceInfo> devices = new ArrayList<DeviceInfo>();

        for (int i = 0; i < 2; i++) {

            KineticDevice device = new KineticDevice();

            DeviceInfo dstatus = new DeviceInfo();

            dstatus.setDevice(device);

            device.setSerialNumber(String.valueOf(i));

            devices.add(dstatus);
        }

        DiscoverResponse resp = new DiscoverResponse();

        resp.setDiscoId("1234567890");

        resp.setDevices(devices);

        return resp;
    }

    private RestResponse ping(RestRequest request) {

        // do ping service, below is an example
        PingResponse response = new PingResponse();

        if (request.getDiscoId() != null) {

            // do discover with the discoId

        } else {

            // set response data, this echos the request
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

            response.setDevices(respDevices);
        }

        return response;
    }

}
