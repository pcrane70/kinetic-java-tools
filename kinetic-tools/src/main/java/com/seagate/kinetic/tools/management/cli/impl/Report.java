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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            System.out.println("[Failed]" + KineticDevice.toJson(device) + "\n"
                    + msg);
        } else {
            failed.put(device, EMPTY);
        }
    }

    public synchronized void reportSuccess(KineticDevice device, String msg) {
        if (msg != null) {
            succeed.put(device, msg);
            System.out.println("[Succeed]" + KineticDevice.toJson(device));
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

    void printSummary() {
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();
        int totalDevices = (null == devices ? (failedDevices + succeedDevices)
                : devices.size());

        if (succeedDevices > 0) {
            System.out.println("\nSucceed:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("\nFailed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")\n");
    }
}
