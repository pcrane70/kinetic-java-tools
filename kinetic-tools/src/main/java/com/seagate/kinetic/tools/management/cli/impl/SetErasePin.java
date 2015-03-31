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

public class SetErasePin extends DefaultExecuter {
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

    public void setErasePin() throws InterruptedException, KineticException,
            JsonGenerationException, JsonMappingException, IOException {
        CountDownLatch latch = new CountDownLatch(devices.size());
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("Start set erase pin...");

        for (KineticDevice device : devices) {
            pool.execute(new SetErasePinThread(device, oldErasePin,
                    newErasePin, latch, useSsl, clusterVersion, identity, key,
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
            System.out.println("The following devices set erase pin succeed:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("The following devices set erase pin failed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
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
                adminClient.setErasePin(oldErasePin, newErasePin);

                synchronized (this) {
                    succeed.put(device, "");
                }

                System.out.println("[Succeed]" + KineticDevice.toJson(device));

                latch.countDown();
            } catch (KineticException e) {
                synchronized (this) {
                    failed.put(device, "");
                }

                try {
                    System.out.println("[Failed]"
                            + KineticDevice.toJson(device));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                latch.countDown();
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
