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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.ConnectionListener;
import kinetic.client.KineticException;

import com.seagate.kinetic.common.lib.KineticMessage;
import com.seagate.kinetic.proto.Kinetic.Command.GetLog.Configuration;
import com.seagate.kinetic.proto.Kinetic.Command.GetLog.Configuration.Interface;
import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.ping.PingResponse;

public class PingReachableDrive extends AbstractCommand {
    private static final String SUBNET_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final int UNSOLICITED_MSG_MAX_WAIT_MS = 5000;
    private static final int SUB_NET_LENGTH = 255;
    private static final int TLS_PORT = 8443;
    private static final int PORT = 8123;
    private String driveListOutputFile;
    private String subnetPrefix = null;
    private AtomicInteger unSolicitedCounter = new AtomicInteger(0);
    private AtomicInteger reqCounter = new AtomicInteger(0);

    public PingReachableDrive(String subnetPrefixOrDriveInputFilePath,
            String driveListOutputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                subnetPrefixOrDriveInputFilePath);
        this.driveListOutputFile = driveListOutputFile;

        if (validateSubnet(subnetPrefixOrDriveInputFilePath)) {
            this.subnetPrefix = subnetPrefixOrDriveInputFilePath;
        }
    }

    private void waitUnSolicitedMesesages(int maxWaitMilliSeconds) {
        boolean stop = false;
        long start = System.currentTimeMillis();
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                if ((System.currentTimeMillis() - start > maxWaitMilliSeconds || unSolicitedCounter
                        .get() == 0) && reqCounter.get() == 0) {
                    stop = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!stop);

    }

    private void pingReachableDriveViaSubnet() throws Exception {
        UnSolicitedConnectionListener listener = new UnSolicitedConnectionListener();

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        List<String> inet4 = null;
        KineticDevice device = null;
        for (int i = 0; i < SUB_NET_LENGTH; i++) {
            inet4 = new ArrayList<String>();
            inet4.add(subnetPrefix + "." + i);
            device = new KineticDevice();
            device.setInet4(inet4);
            device.setPort(PORT);
            device.setTlsPort(TLS_PORT);

            threads.add(new PingReachableDriveThread(device, listener));
        }
        poolExecuteThreadsInGroups(threads);

        waitUnSolicitedMesesages(UNSOLICITED_MSG_MAX_WAIT_MS);
    }

    private void pingReachableDriveViaDriveList() throws Exception {
        UnSolicitedConnectionListener listener = new UnSolicitedConnectionListener();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new PingReachableDriveThread(device, listener));
        }
        poolExecuteThreadsInGroups(threads);

        waitUnSolicitedMesesages(UNSOLICITED_MSG_MAX_WAIT_MS);
    }

    private String persistToFile(List<KineticDevice> deviceList, String filePath)
            throws Exception {
        assert (filePath != null);
        assert (deviceList != null);

        FileOutputStream fos = new FileOutputStream(new File(filePath));
        StringBuffer sb = new StringBuffer();
        for (KineticDevice device : deviceList) {
            sb.append(KineticDevice.toJson(device));
            sb.append("\n");
        }
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();

        return sb.toString();
    }

    private boolean validateSubnet(String subnet) {
        Pattern pattern = Pattern.compile(SUBNET_PATTERN);
        Matcher matcher = pattern.matcher(subnet);

        return matcher.matches();
    }

    class PingReachableDriveThread extends AbstractWorkThread {
        public PingReachableDriveThread(KineticDevice device,
                UnSolicitedConnectionListener listener) throws KineticException {
            super(device);
            adminClientConfig.setConnectionListener(listener);
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
                runTask();
            } catch (KineticException e) {
                report.reportFailure(device, null);
            } catch (KineticToolsException e) {
                report.reportFailure(device, null);
            } finally {
                reqCounter.getAndDecrement();
                try {
                    if (null != adminClient) {
                        adminClient.close();
                    }
                } catch (KineticException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }

        @Override
        void runTask() throws KineticToolsException {
            unSolicitedCounter.getAndIncrement();
        }
    }

    class UnSolicitedConnectionListener implements ConnectionListener {
        @Override
        public void onMessage(KineticMessage message) {
            KineticDevice device = new KineticDevice();
            Configuration configuration = null;
            if (null != message && null != message.getCommand()
                    && null != message.getCommand().getBody()
                    && null != message.getCommand().getBody().getGetLog())

                configuration = message.getCommand().getBody().getGetLog()
                        .getConfiguration();

            if (null != configuration) {
                List<Interface> itfs = configuration.getInterfaceList();
                List<String> inet4 = new ArrayList<String>();
                if (null != itfs && 0 != itfs.size()) {
                    if (null != itfs.get(0)
                            && null != itfs.get(0).getIpv4Address()) {
                        inet4.add(itfs.get(0).getIpv4Address().toStringUtf8());
                    }
                    if (null != itfs.get(1)
                            && null != itfs.get(1).getIpv4Address()) {
                        inet4.add(itfs.get(1).getIpv4Address().toStringUtf8());
                    }
                }
                device.setInet4(inet4);
                device.setModel(configuration.getModel());
                device.setPort(configuration.getPort());
                device.setTlsPort(configuration.getTlsPort());
                device.setFirmwareVersion(configuration.getVersion());
                device.setSerialNumber(configuration.getSerialNumber()
                        .toStringUtf8());
                device.setWwn(configuration.getWorldWideName().toStringUtf8());
            }

            report.reportSuccess(device, "");
            unSolicitedCounter.getAndDecrement();
        }
    }

    @Override
    public void init() throws KineticToolsException {
        if (null == subnetPrefix) {
            super.init();
            reqCounter = new AtomicInteger(devices.size());
        } else {
            reqCounter = new AtomicInteger(SUB_NET_LENGTH);
        }

    }

    @Override
    public void execute() throws KineticToolsException {
        if (null == subnetPrefix) {
            try {
                pingReachableDriveViaDriveList();
            } catch (Exception e) {
                throw new KineticToolsException(e);
            }
        } else {
            try {
                pingReachableDriveViaSubnet();
            } catch (Exception e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void done() throws KineticToolsException {
        List<KineticDevice> reachableDevices = report.getSucceedDevices();

        if (null == subnetPrefix) {
            super.done();
            try {
                PingResponse response = new PingResponse();
                report.persistReport(response,
                        "ping_" + System.currentTimeMillis(),
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (IOException e) {
                throw new KineticToolsException(e);
            }
        } else {
            if (reachableDevices.size() > 0) {
                System.out.println("\nDiscovered " + reachableDevices.size()
                        + " drives via subnet: " + subnetPrefix
                        + ", persist drives info in " + driveListOutputFile);
            }
            try {
                persistToFile(reachableDevices, driveListOutputFile);
            } catch (Exception e) {
                throw new KineticToolsException(e);
            }
        }
    }
}
