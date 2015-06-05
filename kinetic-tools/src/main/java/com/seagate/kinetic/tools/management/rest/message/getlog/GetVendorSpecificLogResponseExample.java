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
package com.seagate.kinetic.tools.management.rest.message.getlog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;

public class GetVendorSpecificLogResponseExample {

    public static void main(String[] args) {

        GetLogResponse resp = new GetLogResponse();

        List<DeviceLog> listOfLogs = new ArrayList<DeviceLog>();

        for (int i = 0; i < 1; i++) {
            DeviceLog dlog = new DeviceLog();

            DeviceStatus status = new DeviceStatus();

            DeviceId did = new DeviceId();
            did.setPort(8123 + i);
            did.setTlsPort(8443 + i);

            status.setDevice(did);
            dlog.setDeviceStatus(status);

            listOfLogs.add(dlog);

            resp.setDeviceLogs(listOfLogs);
        }

        try {
            resp.setValue("vendor specific device log".getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(resp.toJson());
    }
}
