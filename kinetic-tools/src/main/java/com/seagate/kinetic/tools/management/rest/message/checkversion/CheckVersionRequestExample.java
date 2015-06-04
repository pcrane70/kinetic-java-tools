package com.seagate.kinetic.tools.management.rest.message.checkversion;

public class CheckVersionRequestExample {

    public static void main(String[] args) {

        CheckVersionRequest req = new CheckVersionRequest();
        req.setExpectFirmwareVersion("2.7.3");

        System.out.println(req.toJson());
    }

}
