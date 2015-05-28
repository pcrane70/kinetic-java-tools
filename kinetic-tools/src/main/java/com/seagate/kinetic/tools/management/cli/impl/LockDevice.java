package com.seagate.kinetic.tools.management.cli.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public class LockDevice extends DefaultExecuter {
    private static final int BATCH_THREAD_NUMBER = 20;
    private final Logger logger = Logger.getLogger(LockDevice.class.getName());
    private byte[] lockPin;

    public LockDevice(String driveInputFile, String lockPinInString,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        this.lockPin = null;
        parsePin(lockPinInString);
        loadDevices(driveInputFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
    }

    private void parsePin(String lockPinInString) {
        if (null != lockPinInString) {
            this.lockPin = lockPinInString.getBytes(Charset.forName("UTF-8"));
        }
    }

    public void lockDevice() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        System.out.println("Start lock device...");

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                pool.execute(new LockDeviceThread(devices.get(num), latch,
                        lockPin, useSsl, clusterVersion, identity, key,
                        requestTimeout));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;

            pool.execute(new LockDeviceThread(devices.get(num), latchRest,
                    lockPin, useSsl, clusterVersion, identity, key,
                    requestTimeout));
        }

        latchRest.await();

        pool.shutdown();

        int totalDevices = devices.size();
        int succeedDevices = succeed.size();
        int failedDevices = failed.size();

        assert (succeedDevices + failedDevices == totalDevices);

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();

        if (succeedDevices > 0) {
            System.out
                    .println("\nThe following devices were locked successfully");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("\nThe following devices were locked failed");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
        
        System.out.println("\n(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")\n");
    }

    class LockDeviceThread implements Runnable {
        private KineticDevice device = null;
        private CountDownLatch latch = null;
        private byte[] lockPin = null;
        private AdminClientConfiguration adminClientConfig = null;
        private KineticAdminClient adminClient = null;

        public LockDeviceThread(KineticDevice device, CountDownLatch latch,
                byte[] lockPin, boolean useSsl, long clusterVersion,
                long identity, String key, long requestTimeout)
                throws KineticException {
            this.device = device;
            this.latch = latch;
            this.lockPin = lockPin;

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
            adminClientConfig.setClusterVersion(clusterVersion);
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);

                adminClient.lockDevice(lockPin);
                succeed.put(device, "");

                System.out.println("[Succeed]" + KineticDevice.toJson(device));
            } catch (KineticException e) {
                failed.put(device, "");

                try {
                    System.out.println("[Failed]"
                            + KineticDevice.toJson(device) + "\n"
                            + e.getMessage());
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }

            } catch (JsonGenerationException e) {
                System.out.println(e.getMessage());
            } catch (JsonMappingException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
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
