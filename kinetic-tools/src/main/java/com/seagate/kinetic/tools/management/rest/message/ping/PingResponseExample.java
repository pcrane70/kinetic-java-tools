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
package com.seagate.kinetic.tools.management.rest.message.ping;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;

public class PingResponseExample {

    /**
     * This generates a simple example of <code>PingResponse</code>message.
     * 
     * @param args
     */
    public static void main(String[] args) {

        List<DeviceStatus> devices = new ArrayList<DeviceStatus>();

        for (int i = 0; i < 2; i++) {

            DeviceId device = new DeviceId();

            DeviceStatus dstatus = new DeviceStatus();

            dstatus.setDevice(device);

            // List<String> ips = {"127.0.0.1"};
            device.setWwn(String.valueOf(i));

            devices.add(dstatus);
        }

        PingResponse resp = new PingResponse();

        resp.setDevices(devices);

        System.out.println(resp.toJson());
    }

}
