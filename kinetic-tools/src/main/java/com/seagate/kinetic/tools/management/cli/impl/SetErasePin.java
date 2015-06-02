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

public class SetErasePin extends DefaultExecuter {
    private static final int BATCH_THREAD_NUMBER = 20;
    private final Logger logger = Logger.getLogger(SetErasePin.class.getName());
    private byte[] oldErasePin;
    private byte[] newErasePin;

    public SetErasePin(String oldErasePinInString, String newErasePinInString,
            String drivesInputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        this.oldErasePin = null;
        this.newErasePin = null;
        parsePin(oldErasePinInString, newErasePinInString);
        loadDevices(drivesInputFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
    }

    private void parsePin(String oldErasePin, String newErasePin) {
        if (null != oldErasePin) {
            this.oldErasePin = oldErasePin.getBytes(Charset.forName("UTF-8"));

        }

        if (null != newErasePin) {
            this.newErasePin = newErasePin.getBytes(Charset.forName("UTF-8"));
        }
    }

    public void setErasePin() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        System.out.println("Start set erase pin...");

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                pool.execute(new SetErasePinThread(devices.get(num),
                        oldErasePin, newErasePin, latch, useSsl,
                        clusterVersion, identity, key, requestTimeout));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;

            pool.execute(new SetErasePinThread(devices.get(num), oldErasePin,
                    newErasePin, latchRest, useSsl, clusterVersion, identity,
                    key, requestTimeout));
        }

        latchRest.await();

        pool.shutdown();

        int totalDevices = devices.size();
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();

        assert (failedDevices + succeedDevices == totalDevices);

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();

        if (succeedDevices > 0) {
            System.out.println("\nThe following devices set erase pin succeed:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("\nThe following devices set erase pin failed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
        
        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")\n");
    }

    class SetErasePinThread implements Runnable {
        private KineticDevice device = null;
        private KineticAdminClient adminClient = null;
        private AdminClientConfiguration adminClientConfig = null;
        private byte[] oldErasePin = null;
        private byte[] newErasePin = null;
        private CountDownLatch latch = null;

        public SetErasePinThread(KineticDevice device, byte[] oldErasePin,
                byte[] newErasePin, CountDownLatch latch, boolean useSsl,
                long clusterVersion, long identity, String key,
                long requestTimeout) throws KineticException {
            this.device = device;
            this.oldErasePin = oldErasePin;
            this.newErasePin = newErasePin;
            this.latch = latch;

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
                adminClientConfig.setThreadPoolAwaitTimeOut(5000);
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

                adminClient.setErasePin(oldErasePin, newErasePin);

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
