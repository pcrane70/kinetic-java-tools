package com.seagate.kinetic.tools.management.rest.message;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class RestMessageExample {

    public static void main(String[] args) {

        RestMessage req = new RestMessage();

        String request = req.toJson();

        System.out.println(request);

        RestMessage req2 = (RestMessage) MessageUtil.fromJson(request,
                req.getClass());

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
