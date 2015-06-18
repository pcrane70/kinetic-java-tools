package com.seagate.kinetic.tools.management.rest.message.setclversion;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class SetClusterVersionRequestExample {

    public static void main(String[] args) {

        SetClusterVersionRequest req = new SetClusterVersionRequest();

        List<DeviceId> devices = new ArrayList<DeviceId>();

        DeviceId deviceId = new DeviceId();

        deviceId.setWwn("1234");
        String[] ips = { "127.0.0.1" };
        deviceId.setIps(ips);

        devices.add(deviceId);

        req.setDevices(devices);

        req.setClversion(0);
        req.setNewClversion(1);

        String request = req.toJson();

        System.out.println(request);

        SetClusterVersionRequest req2 = (SetClusterVersionRequest) MessageUtil
                .fromJson(
                request,
 SetClusterVersionRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
