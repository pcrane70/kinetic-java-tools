package com.seagate.kinetic.tools.management.rest.message.lockdevice;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class LockDeviceRequestExample {

    public static void main(String[] args) {

        LockDeviceRequest req = new LockDeviceRequest();

        req.setDiscoId("1234");

        req.setPin("777");

        String request = req.toJson();

        System.out.println(request);

        LockDeviceRequest req2 = (LockDeviceRequest) MessageUtil.fromJson(
                request, LockDeviceRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
