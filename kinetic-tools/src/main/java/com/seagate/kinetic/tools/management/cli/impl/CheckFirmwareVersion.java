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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;
import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.common.util.MessageUtil;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.checkversion.CheckVersionResponse;

public class CheckFirmwareVersion extends AbstractCommand {
    private String expectedFirewareVersion;

    public CheckFirmwareVersion(String expectedFirewareVersion,
            String drivesLogFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesLogFile);
        this.expectedFirewareVersion = expectedFirewareVersion;
    }

    public CheckFirmwareVersion(String expectedFirewareVersion,
            List<DeviceId> deviceIds, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, deviceIds);
        this.expectedFirewareVersion = expectedFirewareVersion;
    }

    private void checkFirmwareVersion() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new VersionCheckThread(device, expectedFirewareVersion));
        }
        poolExecuteThreadsInGroups(threads);
    }

    class VersionCheckThread extends AbstractWorkThread {
        private String expectedFirewareVersion = null;

        public VersionCheckThread(KineticDevice device,
                String expectedFirewareVersion) throws KineticException {
            super(device);
            this.expectedFirewareVersion = expectedFirewareVersion;
        }

        @Override
        void runTask() throws KineticToolsException {
            List<KineticLogType> listOfLogType = new ArrayList<KineticLogType>();
            listOfLogType.add(KineticLogType.CONFIGURATION);
            KineticLog kineticLog;
            try {
                kineticLog = adminClient.getLog(listOfLogType);
                String version = null;
                if (null != kineticLog && null != kineticLog.getConfiguration()) {
                    version = kineticLog.getConfiguration().getVersion();
                    device.setFirmwareVersion(version);
                }

                if (version.equals(expectedFirewareVersion)) {
                    report.reportSuccess(device);
                } else {
                    report.reportFailure(device, "Firmware version mismatch");
                }
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }

        }
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            checkFirmwareVersion();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        CheckVersionResponse response = new CheckVersionResponse();
        DeviceId device = null;
        DeviceStatus dstatus = null;
        List<DeviceStatus> respDevices = new ArrayList<DeviceStatus>();

        for (KineticDevice kineticDevice : report.getSucceedDevices()) {
            device = toDeviceId(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            respDevices.add(dstatus);
        }

        for (KineticDevice kineticDevice : report.getFailedDevices()) {
            device = toDeviceId(kineticDevice);
            dstatus = new DeviceStatus();
            dstatus.setDevice(device);
            dstatus.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            dstatus.setMessage("Expect " + expectedFirewareVersion + " but "
                    + kineticDevice.getFirmwareVersion());
            respDevices.add(dstatus);
        }

        response.setDevices(respDevices);
        String toolHome = System.getProperty("kinetic.tools.out", ".");
        String rootDir = toolHome + File.separator + "out" + File.separator
                + "checkversion_" + System.currentTimeMillis();

        try {
            report.persistReport(MessageUtil.toJson(response), rootDir);
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }
}
