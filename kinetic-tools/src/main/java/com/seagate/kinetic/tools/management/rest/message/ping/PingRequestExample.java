package com.seagate.kinetic.tools.management.rest.message.ping;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class PingRequestExample {

    /**
     * This generates a simple example of the <code>PingRequest</code> message.
     * 
     * @param args
     */
    public static void main(String[] args) {

        PingRequest req = new PingRequest();

        List<DeviceId> devices = new ArrayList<DeviceId>();

        DeviceId deviceId = new DeviceId();

        deviceId.setWwn("1234");
        String[] ips = { "127.0.0.1" };
        deviceId.setIps(ips);

        devices.add(deviceId);

        req.setDevices(devices);

        String request = req.toJson();

        System.out.println(request);

        PingRequest req2 = (PingRequest) MessageUtil.fromJson(request,
                PingRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
