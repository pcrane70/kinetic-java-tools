package com.seagate.kinetic.tools.management.rest.message.erasedevice;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class SecureEraseRequestExample {

    public static void main(String[] args) {

        SecureEraseRequest req = new SecureEraseRequest();

        req.setDiscoId("1234");

        req.setPin("777");

        String request = req.toJson();

        System.out.println(request);

        SecureEraseRequest req2 = (SecureEraseRequest) MessageUtil.fromJson(
                request, SecureEraseRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
