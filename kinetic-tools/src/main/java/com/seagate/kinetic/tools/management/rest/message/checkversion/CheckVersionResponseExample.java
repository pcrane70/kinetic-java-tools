package com.seagate.kinetic.tools.management.rest.message.checkversion;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;

public class CheckVersionResponseExample {

    public static void main(String[] args) {

        CheckVersionResponse resp = new CheckVersionResponse();
        List<DeviceStatus> statusList = new ArrayList<DeviceStatus>();
        
        DeviceStatus status = new DeviceStatus();
        DeviceId id = new DeviceId();
        String[] ip = { "127.0.0.1" };
        id.setIps(ip);
        
        status.setDevice(id);

        status.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        status.setMessage("expect version: 2.7.3, device version: 2.7.2");

        statusList.add(status);

        resp.setDevices(statusList);

        System.out.println(resp.toJson());

    }

}
