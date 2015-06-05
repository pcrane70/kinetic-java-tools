package com.seagate.kinetic.tools.management.rest.message.erasedevice;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class InstantEraseRequestExample {

    public static void main(String[] args) {

        InstantEraseRequest req = new InstantEraseRequest();

        req.setDiscoId("1234");

        req.setPin("777");

        String request = req.toJson();

        System.out.println(request);

        InstantEraseRequest req2 = (InstantEraseRequest) MessageUtil.fromJson(
                request, InstantEraseRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
