package com.seagate.kinetic.tools.management.rest.message.setpin;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class SetErasePinRequestExample {

    public static void main(String[] args) {

        SetErasePinRequest req = new SetErasePinRequest();

        List<DeviceId> devices = new ArrayList<DeviceId>();

        DeviceId deviceId = new DeviceId();

        deviceId.setWwn("1234");
        String[] ips = { "127.0.0.1" };
        deviceId.setIps(ips);

        devices.add(deviceId);

        req.setDevices(devices);

        req.setOldPin("123");
        req.setNewPin("456");

        String request = req.toJson();

        System.out.println(request);

        SetErasePinRequest req2 = (SetErasePinRequest) MessageUtil.fromJson(
                request,
                SetErasePinRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
