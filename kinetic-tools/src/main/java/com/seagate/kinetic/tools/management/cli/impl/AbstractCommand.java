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

abstract class AbstractCommand implements Command {
    protected static final int BATCH_THREAD_NUMBER = 20;
    protected List<KineticDevice> devices = new ArrayList<KineticDevice>();
    protected Report report = new Report();
    protected BasicSettings basicSettings = new BasicSettings();
    protected StringBuffer sb = new StringBuffer();

    public AbstractCommand(boolean useSsl, long clusterVersion, long identity,
            String key, long requestTimeout, String drivesLogFile) {
        basicSettings.setClusterVersion(clusterVersion)
                .setDrivesLogFile(drivesLogFile).setIdentity(identity)
                .setKey(key).setRequestTimeout(requestTimeout)
                .setUseSsl(useSsl);
    }

    public AbstractCommand(String drivesLogFile) {
        basicSettings.setDrivesLogFile(drivesLogFile);
    }

    protected void loadDevices(String drivesInputFile) throws IOException {
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
            adminClientConfig
                    .setRequestTimeoutMillis(getRequestTimeout() * 1000);
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
                runTask();
            } catch (KineticException e) {
                report.reportFailure(device, e.getMessage());
            } catch (KineticToolsException e) {
                report.reportFailure(device, e.getMessage());
            } finally {
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

        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        abstract void runTask() throws KineticToolsException;
    }

    @Override
    public Report getReport() {
        return report;
    }
}
