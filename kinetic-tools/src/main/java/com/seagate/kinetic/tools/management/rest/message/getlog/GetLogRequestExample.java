package com.seagate.kinetic.tools.management.rest.message.getlog;

import kinetic.admin.KineticLogType;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class GetLogRequestExample {

    public static void main(String[] args) {

        GetlogRequest req = new GetlogRequest();

        req.setLogType(KineticLogType.CONFIGURATION);

        String request = req.toJson();

        System.out.println(request);

        GetlogRequest req2 = (GetlogRequest) MessageUtil.fromJson(request,
                GetlogRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);
    }

}
