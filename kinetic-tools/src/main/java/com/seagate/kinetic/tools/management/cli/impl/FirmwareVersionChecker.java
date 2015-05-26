package com.seagate.kinetic.tools.management.cli.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;
import kinetic.client.KineticException;

public class FirmwareVersionChecker extends DefaultExecuter {
    private static final int BATCH_THREAD_NUMBER = 20;
    private final Logger logger = Logger.getLogger(FirmwareVersionChecker.class
            .getName());
    private String expectedFirewareVersion;

    public FirmwareVersionChecker(String expectedFirewareVersion,
            String nodesLogFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        this.expectedFirewareVersion = expectedFirewareVersion;
        loadDevices(nodesLogFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
    }

    public void checkFirmwareVersion() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        System.out.println("Start verify firmware version...");

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                pool.execute(new VersionCheckThread(devices.get(num), latch,
                        useSsl, clusterVersion, identity, key, requestTimeout,
                        expectedFirewareVersion));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;

            pool.execute(new VersionCheckThread(devices.get(num), latchRest,
                    useSsl, clusterVersion, identity, key, requestTimeout,
                    expectedFirewareVersion));
        }

        latchRest.await();

        pool.shutdown();

        int totalDevices = devices.size();
        int succeedDevices = succeed.size();
        int failedDevices = failed.size();

        System.out.flush();

        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")");

        if (succeedDevices > 0) {
            System.out
                    .println("The following devices have same firmware version as expected:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out
                    .println("The following devices have different firmware version than expected:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
    }

    class VersionCheckThread implements Runnable {
        private KineticDevice device = null;
        private KineticAdminClient adminClient = null;
        private AdminClientConfiguration adminClientConfig = null;
        private CountDownLatch latch = null;
        private String expectedFirewareVersion = null;

        public VersionCheckThread(KineticDevice device, CountDownLatch latch,
                boolean useSsl, long clusterVersion, long identity, String key,
                long requestTimeout, String expectedFirewareVersion)
                throws KineticException {
            this.device = device;
            this.latch = latch;
            this.expectedFirewareVersion = expectedFirewareVersion;

            if (null == device || 0 == device.getInet4().size()
                    || device.getInet4().isEmpty()) {
                throw new KineticException(
                        "device is null or no ip addresses in device.");
            }

            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(device.getInet4().get(0));
            adminClientConfig.setUseSsl(useSsl);
            if (useSsl) {
                adminClientConfig.setPort(device.getTlsPort());
            } else {
                adminClientConfig.setPort(device.getPort());
            }
            adminClientConfig.setClusterVersion(clusterVersion);
            adminClientConfig.setUserId(identity);
            adminClientConfig.setKey(key);
            adminClientConfig.setRequestTimeoutMillis(requestTimeout);
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);

                List<KineticLogType> listOfLogType = new ArrayList<KineticLogType>();
                listOfLogType.add(KineticLogType.CONFIGURATION);
                KineticLog kineticLog = adminClient.getLog(listOfLogType);

                String version = null;
                if (null != kineticLog && null != kineticLog.getConfiguration()) {
                    version = kineticLog.getConfiguration().getVersion();
                }

                if (version.equals(expectedFirewareVersion)) {
                    succeed.put(device, "");

                    System.out.println("[Succeed]"
                            + KineticDevice.toJson(device));
                } else {
                    failed.put(device, "");

                    try {
                        System.out.println("[Failed]"
                                + KineticDevice.toJson(device) + "\n");
                    } catch (IOException e1) {
                        System.out.println(e1.getMessage());
                    }
                }
            } catch (Exception e) {
                synchronized (this) {
                    failed.put(device, "");
                }

                try {
                    System.out.println("[Failed]"
                            + KineticDevice.toJson(device) + "\n"
                            + e.getMessage());
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
            } finally {
                try {
                    if (null != adminClient) {
                        adminClient.close();
                    }
                } catch (KineticException e) {
                    logger.warning(e.getMessage());
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                } finally {
                    latch.countDown();
                }
            }
        }
    }
}
