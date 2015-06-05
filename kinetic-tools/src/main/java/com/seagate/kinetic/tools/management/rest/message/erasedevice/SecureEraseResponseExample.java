package com.seagate.kinetic.tools.management.rest.message.erasedevice;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;

public class SecureEraseResponseExample {

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

        SecureEraseResponse resp = new SecureEraseResponse();

        resp.setDevices(devices);

        System.out.println(resp.toJson());
    }

}
