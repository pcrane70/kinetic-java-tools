package com.seagate.kinetic.tools.management.rest.message.getlog;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.Capacity;
import kinetic.admin.Limits;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;

public class GetLogResponseExample {

    public static void main(String[] args) {

        GetLogResponse resp = new GetLogResponse();

        List<DeviceLog> listOfLogs = new ArrayList<DeviceLog>();

        for (int i = 0; i < 2; i++) {
            DeviceLog dlog = new DeviceLog();

            Capacity c = new Capacity();
            c.setPortionFull(0.2f);

            dlog.setCapacity(c);

            Limits limits = new Limits();
            limits.setMaxConnections(100);
            limits.setMaxKeySize(4096);
            dlog.setLimits(limits);

            DeviceStatus status = new DeviceStatus();

            DeviceId did = new DeviceId();
            did.setPort(8123 + i);
            did.setTlsPort(8443 + i);

            status.setDevice(did);
            dlog.setDeviceStatus(status);

            listOfLogs.add(dlog);

            resp.setDeviceLogs(listOfLogs);
        }

        System.out.println(resp.toJson());
    }
}
