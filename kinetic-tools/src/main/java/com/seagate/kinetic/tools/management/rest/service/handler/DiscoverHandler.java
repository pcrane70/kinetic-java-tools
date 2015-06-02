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

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;
import com.seagate.kinetic.tools.management.rest.message.DeviceInfo;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverRequest;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverResponse;
import com.seagate.kinetic.tools.management.rest.service.ServiceContext;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * Discover service handler.
 * 
 * @author chiaming
 *
 */
public class DiscoverHandler implements ServiceHandler {

    public DiscoverHandler() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void service(ServiceContext context) {

        List<DeviceInfo> devices = new ArrayList<DeviceInfo>();

        for (int i = 0; i < 2; i++) {

            KineticDevice device = new KineticDevice();

            DeviceInfo dstatus = new DeviceInfo();

            dstatus.setDevice(device);

            device.setSerialNumber(String.valueOf(i));

            devices.add(dstatus);
        }

        DiscoverResponse resp = new DiscoverResponse();

        resp.setDevices(devices);

        context.setResponseMessage(resp);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return DiscoverRequest.class;
    }

}
