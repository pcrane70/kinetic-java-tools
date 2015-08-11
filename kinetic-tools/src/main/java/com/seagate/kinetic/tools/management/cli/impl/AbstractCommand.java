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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;

abstract class AbstractCommand implements Command {
    private static final String UNKNOWN = "unknown";
    private static final int MILLI_SECOND_IN_UNIT = 1000;
    protected static final int BATCH_THREAD_NUMBER = 20;
    protected List<KineticDevice> devices = new ArrayList<KineticDevice>();
    protected Report report = new Report();
    protected BasicSettings basicSettings = new BasicSettings();
    protected StringBuffer sb = new StringBuffer();
    protected List<DeviceId> deviceIds = null;

    protected AdminClientRegister adminClientRegister = null;

    public AbstractCommand(boolean useSsl, long clusterVersion, long identity,
            String key, long requestTimeout, String drivesLogFile) {
        basicSettings.setClusterVersion(clusterVersion)
                .setDrivesLogFile(drivesLogFile).setIdentity(identity)
                .setKey(key).setRequestTimeout(requestTimeout)
                .setUseSsl(useSsl);
    }

    public AbstractCommand(boolean useSsl, long clusterVersion, long identity,
            String key, long requestTimeout, List<DeviceId> deviceIds) {
        basicSettings.setClusterVersion(clusterVersion).setDrivesLogFile(null)
                .setIdentity(identity).setKey(key)
                .setRequestTimeout(requestTimeout).setUseSsl(useSsl);
        this.deviceIds = deviceIds;
    }

    public AbstractCommand(String drivesLogFile) {
        basicSettings.setDrivesLogFile(drivesLogFile);
    }

    protected void loadDevices(String drivesInputFile) throws IOException,
            KineticToolsException {
        if (null == drivesInputFile) {
            if (null == deviceIds) {
                throw new KineticToolsException("No dirves input information");
            } else {
                for (DeviceId deviceId : deviceIds) {
                    devices.add(toKineticDevice(deviceId));
                }
            }
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(
                    drivesInputFile));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                devices.add(KineticDevice.fromJson(line));
            }

            reader.close();
        }

        report.registerDevices(devices);
    }

    public boolean isUseSsl() {
        return basicSettings.isUseSsl();
    }

    public long getClusterVersion() {
        return basicSettings.getClusterVersion();
    }

    public long getIdentity() {
        return basicSettings.getIdentity();
    }

    public String getKey() {
        return basicSettings.getKey();
    }

    public long getRequestTimeout() {
        return basicSettings.getRequestTimeout();
    }

    public String getDrivesLogFile() {
        return basicSettings.getDrivesLogFile();
    }

    @Override
    public void init() throws KineticToolsException {
        try {
            loadDevices(this.getDrivesLogFile());
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        try {
            report.printSummary();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    protected KineticDevice toKineticDevice(DeviceId deviceId) {
        KineticDevice kineticDevice = new KineticDevice();
        kineticDevice.setFirmwareVersion(UNKNOWN);
        List<String> ips = new ArrayList<String>();
        for (String ip : deviceId.getIps()) {
            ips.add(ip);
        }
        kineticDevice.setInet4(ips);
        kineticDevice.setModel(UNKNOWN);
        kineticDevice.setPort(deviceId.getPort());
        kineticDevice.setSerialNumber(UNKNOWN);
        kineticDevice.setTlsPort(deviceId.getTlsPort());
        kineticDevice.setWwn(deviceId.getWwn());

        return kineticDevice;
    }

    protected DeviceId toDeviceId(KineticDevice kineticDevice) {
        DeviceId device = new DeviceId();
        String[] ips;
        device.setPort(kineticDevice.getPort());
        device.setTlsPort(kineticDevice.getTlsPort());
        device.setWwn(kineticDevice.getWwn());
        ips = new String[kineticDevice.getInet4().size()];
        ips = kineticDevice.getInet4().toArray(ips);
        device.setIps(ips);
        return device;
    }

    protected void poolExecuteThreadsInGroups(List<AbstractWorkThread> threads)
            throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        int batchTime = threads.size() / BATCH_THREAD_NUMBER;
        int restIpCount = threads.size() % BATCH_THREAD_NUMBER;

        int threadIndex = 0;
        AbstractWorkThread thread = null;
        CountDownLatch latch = null;
        for (int i = 0; i < batchTime; i++) {
            latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                thread = threads.get(threadIndex++);
                thread.setLatch(latch);
                pool.execute(thread);
            }

            latch.await();
        }

        latch = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            thread = threads.get(threadIndex++);
            thread.setLatch(latch);
            pool.execute(thread);
        }

        latch.await();
        pool.shutdown();
    }

    protected abstract class AbstractWorkThread implements Runnable {
        protected KineticDevice device = null;
        protected KineticAdminClient adminClient = null;
        protected AdminClientConfiguration adminClientConfig = null;
        protected String alternativeHost = null;
        protected CountDownLatch latch = null;

        public AbstractWorkThread(KineticDevice device) throws KineticException {
            this.device = device;

            if (null == device || 0 == device.getInet4().size()
                    || device.getInet4().isEmpty()) {
                throw new KineticException(
                        "device is null or no ip addresses in device.");
            }

            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(device.getInet4().get(0));
            if (device.getInet4().size() > 1) {
                alternativeHost = device.getInet4().get(1);
            }
            adminClientConfig.setUseSsl(isUseSsl());
            if (isUseSsl()) {
                adminClientConfig.setPort(device.getTlsPort());
                adminClientConfig.setThreadPoolAwaitTimeOut(5000);
            } else {
                adminClientConfig.setPort(device.getPort());
            }
            adminClientConfig.setClusterVersion(getClusterVersion());
            adminClientConfig.setUserId(getIdentity());
            adminClientConfig.setKey(getKey());
            adminClientConfig.setRequestTimeoutMillis(getRequestTimeout()
                    * MILLI_SECOND_IN_UNIT);
        }

        @Override
        public void run() {
            try {
                if (null == adminClientRegister) {
                    try {
                        adminClient = KineticAdminClientFactory
                                .createInstance(adminClientConfig);
                    } catch (Exception e) {
                        adminClientConfig.setHost(alternativeHost);
                        adminClient = KineticAdminClientFactory
                                .createInstance(adminClientConfig);
                    }

                } else {
                    String hostAndPort = adminClientConfig.getHost() + ":"
                            + adminClientConfig.getPort();
                    adminClient = adminClientRegister
                            .getKineticAdminClient(hostAndPort);
                    if (null == adminClient) {
                        try {
                            adminClient = KineticAdminClientFactory
                                    .createInstance(adminClientConfig);
                        } catch (Exception e) {
                            adminClientConfig.setHost(alternativeHost);
                            adminClient = KineticAdminClientFactory
                                    .createInstance(adminClientConfig);
                        }

                        adminClientRegister.register(hostAndPort, adminClient);
                    }
                }

                runTask();
            } catch (KineticException e) {
                report.reportFailure(device, e.getMessage());
            } catch (KineticToolsException e) {
                report.reportFailure(device, e.getMessage());
            } finally {
                try {
                    if (null != adminClient && null == adminClientRegister) {
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

        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        abstract void runTask() throws KineticToolsException;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public void setAdminClientRegister(AdminClientRegister adminClientRegister) {
        this.adminClientRegister = adminClientRegister;
    }
}
