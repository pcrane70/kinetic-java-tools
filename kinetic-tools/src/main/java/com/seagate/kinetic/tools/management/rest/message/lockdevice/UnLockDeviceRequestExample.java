package com.seagate.kinetic.tools.management.rest.message.lockdevice;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class UnLockDeviceRequestExample {

    public static void main(String[] args) {

        UnLockDeviceRequest req = new UnLockDeviceRequest();

        req.setDiscoId("1234");

        req.setPin("777");

        String request = req.toJson();

        System.out.println(request);

        UnLockDeviceRequest req2 = (UnLockDeviceRequest) MessageUtil.fromJson(
                request, UnLockDeviceRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);
    }

}
