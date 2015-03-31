package com.seagate.kinetic.tools.management.cli.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public class UnLockDevice extends DefaultExecuter {
    private byte[] unLockPin;

    public UnLockDevice(String drivesInputFile, String unLockPinInString,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        loadDevices(drivesInputFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
        this.unLockPin = null;
        parseUnLockPin(unLockPinInString);
    }

    private void parseUnLockPin(String unLockPinInString) {
        if (null != unLockPinInString) {
            this.unLockPin = unLockPinInString.getBytes(Charset
                    .forName("UTF-8"));
        }
    }

    public void unLockDevice() throws KineticException, InterruptedException,
            JsonGenerationException, JsonMappingException, IOException {
        CountDownLatch latch = new CountDownLatch(devices.size());
        ExecutorService pool = Executors.newCachedThreadPool();

        for (KineticDevice device : devices) {
            pool.execute(new UnLockDeviceThread(device, unLockPin, latch,
                    useSsl, clusterVersion, identity, key, requestTimeout));
        }

        latch.await();
        pool.shutdown();

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();

        int totalDevices = devices.size();
        int succeedDevices = succeed.size();
        int failedDevices = failed.size();

        assert (succeedDevices + failedDevices == totalDevices);

        if (succeedDevices > 0) {
            System.out
                    .println("The following devices were unlocked successfully:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("The following devices were unlocked failed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
    }

    class UnLockDeviceThread implements Runnable {
        private byte[] unLockPin;
        private KineticDevice device;
        private CountDownLatch latch;
        private AdminClientConfiguration adminClientConfig;
        private KineticAdminClient adminClient;

        public UnLockDeviceThread(KineticDevice device, byte[] unLockPin,
                CountDownLatch latch, boolean useSsl, long clusterVersion,
                long identity, String key, long requestTimeout)
                throws KineticException {
            this.unLockPin = unLockPin;
            this.device = device;
            this.latch = latch;

            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(device.getInet4().get(0));
            adminClientConfig.setUseSsl(useSsl);
            if (useSsl) {
                adminClientConfig.setPort(device.getTlsPort());
            } else {
                adminClientConfig.setPort(device.getPort());
            }
            adminClientConfig.setUserId(identity);
            adminClientConfig.setKey(key);
            adminClientConfig.setClusterVersion(clusterVersion);
            adminClientConfig.setRequestTimeoutMillis(requestTimeout);

            adminClient = KineticAdminClientFactory
                    .createInstance(adminClientConfig);

        }

        @Override
        public void run() {
            try {
                adminClient.unLockDevice(unLockPin);

                synchronized (this) {
                    succeed.put(device, "");
                }

                latch.countDown();

                System.out.println("[Succeed]" + KineticDevice.toJson(device));

            } catch (KineticException e) {
                synchronized (this) {
                    failed.put(device, "");
                }

                latch.countDown();

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
                    adminClient.close();
                } catch (KineticException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
