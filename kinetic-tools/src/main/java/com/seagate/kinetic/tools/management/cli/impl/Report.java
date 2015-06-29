/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.seagate.kinetic.tools.management.common.util.MessageUtil;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.RestResponseWithStatus;

public class Report {
    private static final String EMPTY = "";
    private List<KineticDevice> devices = null;
    private Map<KineticDevice, String> failed = new ConcurrentHashMap<KineticDevice, String>();
    private Map<KineticDevice, String> succeed = new ConcurrentHashMap<KineticDevice, String>();
    private Map<KineticDevice, Object> additionMessages = new ConcurrentHashMap<KineticDevice, Object>();

    void registerDevices(List<KineticDevice> devices) {
        this.devices = devices;
    }

    public synchronized void reportFailure(KineticDevice device, String msg) {
        if (msg != null) {
            failed.put(device, msg);
        } else {
            failed.put(device, EMPTY);
        }
    }

    public synchronized void reportSuccess(KineticDevice device, String msg) {
        if (msg != null) {
            succeed.put(device, msg);
        } else {
            failed.put(device, EMPTY);
        }
    }

    public void reportSuccess(KineticDevice device) {
        reportSuccess(device, EMPTY);
    }

    public synchronized List<KineticDevice> getFailedDevices() {
        List<KineticDevice> failiedDevices = new ArrayList<KineticDevice>();
        for (KineticDevice device : failed.keySet()) {
            failiedDevices.add(device);
        }

        return failiedDevices;
    }

    public synchronized List<KineticDevice> getSucceedDevices() {
        List<KineticDevice> succeedDevices = new ArrayList<KineticDevice>();
        for (KineticDevice device : succeed.keySet()) {
            succeedDevices.add(device);
        }

        return succeedDevices;
    }

    public Object getAdditionMessage(KineticDevice device) {
        return additionMessages.get(device);
    }

    public synchronized void setAdditionMessage(KineticDevice device, Object msg) {
        additionMessages.put(device, msg);
    }

    private DeviceId initDevice(KineticDevice kineticDevice) {
        DeviceId device;
        String[] ips;
        device = new DeviceId();
        device.setPort(kineticDevice.getPort());
        device.setTlsPort(kineticDevice.getTlsPort());
        device.setWwn(kineticDevice.getWwn());
        ips = new String[kineticDevice.getInet4().size()];
        ips = kineticDevice.getInet4().toArray(ips);
        device.setIps(ips);
        return device;
    }

    private boolean isReportDisabled() {
        String disableReport = (String) System.getenv("KINETIC_TOOL_DISABLE_REPORT");
        return (disableReport != null && disableReport.equalsIgnoreCase("true"));
    }

    public void persistReport(String reportAsString, String dst)
            throws IOException {
        if (isReportDisabled()) {
            System.out.println("Report disabled\n");
            return;
        }

        File file = new File(dst);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(reportAsString.getBytes());
        fos.flush();
        fos.close();

        System.out.println("Result has been persisted to " + dst);
    }

    public void persistReport(RestResponseWithStatus response, String dst,
            int failureRespCode) throws IOException {
        if (isReportDisabled()) {
            System.out.println("Report disabled\n");
            return;
        }

        persistReport(formatReportAsRestOutput(response, failureRespCode), dst);
    }

    public String formatReportAsRestOutput(RestResponseWithStatus response,
            int failureRespCode) {
        List<DeviceStatus> respDevices = new ArrayList<DeviceStatus>();
        DeviceId device = null;
        DeviceStatus dstatus = null;

        for (KineticDevice kineticDevice : getSucceedDevices()) {
            device = initDevice(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            respDevices.add(dstatus);
        }

        for (KineticDevice kineticDevice : getFailedDevices()) {
            device = initDevice(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            dstatus.setStatus(failureRespCode);
            respDevices.add(dstatus);
        }

        response.setDevices(respDevices);

        return MessageUtil.toJson(response);
    }

    void printSummary() {
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();
        int totalDevices = (null == devices ? (failedDevices + succeedDevices)
                : devices.size());

        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")\n");
    }

}
