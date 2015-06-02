package com.seagate.kinetic.tools.management.rest.message.discover;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;
import com.seagate.kinetic.tools.management.rest.message.DeviceInfo;

public class DiscoverResponseExample {

    public static void main(String[] args) {

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

        System.out.println(resp.toJson());
    }

}
