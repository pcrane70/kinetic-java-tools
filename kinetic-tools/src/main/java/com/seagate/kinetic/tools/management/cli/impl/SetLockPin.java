package com.seagate.kinetic.tools.management.cli.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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

public class SetLockPin extends DeviceLoader {
    private byte[] oldLockPin;
    private byte[] newLockPin;
    private boolean useSsl;
    private long clusterVersion;
    private long identity;
    private String key;
    private long requestTimeout;
    private List<KineticDevice> failed = new ArrayList<KineticDevice>();
    private List<KineticDevice> succeed = new ArrayList<KineticDevice>();

    public SetLockPin(String oldLockPinInString, String newLockPinInString,
            String driveInputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        this.oldLockPin = null;
        this.newLockPin = null;
        this.useSsl = useSsl;
        this.clusterVersion = clusterVersion;
        this.identity = identity;
        this.key = key;
        this.loadDevices(driveInputFile);
        parsePin(oldLockPinInString, newLockPinInString);
    }

    private void parsePin(String oldLockPinInString, String newLockPinInString) {
        this.oldLockPin = oldLockPinInString.getBytes(Charset.forName("UTF-8"));
        this.newLockPin = newLockPinInString.getBytes(Charset.forName("UTF-8"));
    }

    public void setLockPin() throws KineticException, InterruptedException,
            JsonGenerationException, JsonMappingException, IOException {
        CountDownLatch latch = new CountDownLatch(devices.size());
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("Start set lock pin...");

        for (KineticDevice device : devices) {
            pool.execute(new SetLockPinThread(device, oldLockPin, newLockPin,
                    latch, useSsl, clusterVersion, identity, key,
                    requestTimeout));
        }

        // wait all threads finish
        latch.await();
        pool.shutdown();

        int totalDevices = devices.size();
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();

        assert (failedDevices + succeedDevices == totalDevices);

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();
        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")");

        if (succeedDevices > 0) {
            System.out.println("The following devices set lock pin succeed:");
            for (KineticDevice device : succeed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("The following devices set lock pin failed:");
            for (KineticDevice device : failed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
    }

    class SetLockPinThread implements Runnable {
        private KineticDevice device = null;
        private KineticAdminClient adminClient = null;
        private AdminClientConfiguration adminClientConfig = null;
        private byte[] oldLockPin = null;
        private byte[] newLockPin = null;
        private CountDownLatch latch = null;

        public SetLockPinThread(KineticDevice device, byte[] oldLockPin,
                byte[] newLockPin, CountDownLatch latch, boolean useSsl,
                long clusterVersion, long identity, String key,
                long requestTimeout) throws KineticException {
            this.device = device;
            this.oldLockPin = oldLockPin;
            this.newLockPin = newLockPin;
            this.latch = latch;

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

            adminClient = KineticAdminClientFactory
                    .createInstance(adminClientConfig);
        }

        @Override
        public void run() {
            try {
                adminClient.setLockPin(oldLockPin, newLockPin);
                latch.countDown();

                synchronized (this) {
                    succeed.add(device);
                }

                System.out.println("[Succeed]" + KineticDevice.toJson(device));
            } catch (KineticException e) {
                latch.countDown();

                synchronized (this) {
                    failed.add(device);
                }

                try {
                    System.out.println("[Failed]"
                            + KineticDevice.toJson(device));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    adminClient.close();
                } catch (KineticException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
