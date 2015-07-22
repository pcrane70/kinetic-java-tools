package com.seagate.kinetic.tools.management.rest.message.discover;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class DiscoverRequestExample {

    public static void main(String[] args) {

        DiscoverRequest req = new DiscoverRequest();
        req.setScoped(true);
        req.setStartIp("192.127.1.1");
        req.setEndIp("192.127.1.100");

        String request = req.toJson();

        System.out.println(request);

        DiscoverRequest req2 = (DiscoverRequest) MessageUtil.fromJson(request,
                DiscoverRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);
    }

}
