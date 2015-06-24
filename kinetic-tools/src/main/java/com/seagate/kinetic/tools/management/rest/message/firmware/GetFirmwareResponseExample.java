package com.seagate.kinetic.tools.management.rest.message.firmware;

import java.util.ArrayList;
import java.util.List;

public class GetFirmwareResponseExample {

    public static void main(String[] args) {

        GetFirmwareResponse resp = new GetFirmwareResponse();
        List<String> names = new ArrayList<String>();
        names.add("123.run");
        names.add("456.run");

        resp.setFirmwareList(names);

        String response = resp.toJson();

        System.out.println(response);

    }

}
