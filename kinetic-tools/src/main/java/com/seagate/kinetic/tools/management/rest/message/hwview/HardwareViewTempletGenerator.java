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
package com.seagate.kinetic.tools.management.rest.message.hwview;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.service.ServiceConfiguration;

public class HardwareViewTempletGenerator {

    public HardwareViewTempletGenerator() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) throws IOException {

        HardwareViewResponse hwview = new HardwareViewResponse();

        List<Rack> racks = new ArrayList<Rack>();

        for (int ri = 0; ri < 2; ri++) {

            Rack rack = new Rack();
            rack.setId("rack-" + String.valueOf(ri));

            Coordinate cor = new Coordinate();
            cor.setX("rackx-" + ri);
            cor.setY("racky-" + ri);
            cor.setZ("rackz-" + ri);

            rack.setCoordinate(cor);

            List<Chassis> chList = new ArrayList<Chassis>();

            for (int ci = 0; ci < 3; ci++) {

                // chassis
                Chassis cs1 = new Chassis();

                // chassis id
                cs1.setId("chassis-" + String.valueOf(ci));

                String chassisIp0 = "10.2.2." + ci;
                String chassisIp1 = "10.2.3." + ci;
                String[] chasisIps = { chassisIp0, chassisIp1 };

                cs1.setIps(chasisIps);

                // coordinate
                Coordinate co1 = new Coordinate();
                co1.setX("chassisx-" + ci);
                co1.setY("chassisy-" + ci);
                co1.setZ("chassisz-" + ci);

                // set coor
                cs1.setCoordinate(co1);

                List<Device> devices = new ArrayList<Device>();

                for (int i = 0; i < 2; i++) {
                    Device device = new Device();

                    DeviceId did = new DeviceId();
                    did.setWwn("wwn-" + i);
                    String ip = "127.0.0." + (i + 1);
                    String[] ips = { ip };
                    did.setIps(ips);

                    device.setDeviceId(did);

                    Coordinate coordinate = new Coordinate();
                    coordinate.setX("devicex-" + i);
                    coordinate.setY("devicey-" + i);
                    coordinate.setZ("devicez-" + i);

                    device.setCoordinate(coordinate);

                    devices.add(device);
                }

                // set dev id
                cs1.setDevices(devices);

                chList.add(cs1);
            }

            // set chassis
            rack.setChassis(chList);

            // add rack
            racks.add(rack);
        }

        HardwareView config = new HardwareView();
        config.setRacks(racks);

        hwview.setHardwareView(config);

        System.out.println(hwview.toJson());

        String path = ServiceConfiguration.getRestHome()
                + ServiceConfiguration.getHardwareConfigTempletPath()
                + File.separator
                + ServiceConfiguration.getHardwareConfigTempletName();

        FileWriter writer = new FileWriter(path);

        writer.write(config.toJson());

        System.out.println("output written to: " + path);

        writer.close();
    }

}
