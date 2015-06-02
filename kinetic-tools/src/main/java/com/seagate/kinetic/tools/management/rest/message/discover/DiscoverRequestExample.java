package com.seagate.kinetic.tools.management.rest.message.discover;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class DiscoverRequestExample {

    public static void main(String[] args) {

        DiscoverRequest req = new DiscoverRequest();

        String request = req.toJson();

        System.out.println(request);

        DiscoverRequest req2 = (DiscoverRequest) MessageUtil.fromJson(request,
                DiscoverRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);
    }

}
