package com.seagate.kinetic.tools.management.rest.message.getlog;

import kinetic.admin.KineticLogType;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class GetLogRequestExample {

    public static void main(String[] args) {

        GetLogRequest req = new GetLogRequest();

        req.setLogType(KineticLogType.CONFIGURATION);

        String request = req.toJson();

        System.out.println(request);

        GetLogRequest req2 = (GetLogRequest) MessageUtil.fromJson(request,
                GetLogRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);
    }

}
