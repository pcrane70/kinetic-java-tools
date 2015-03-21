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

public class SetErasePin extends DeviceLoader{
    private byte[] oldErasePin;
    private byte[] newErasePin;
    private List<KineticDevice> failed = new ArrayList<KineticDevice>();
    private List<KineticDevice> succeed = new ArrayList<KineticDevice>();

    public SetErasePin(String oldErasePinInString, String newErasePinInString,
            String drivesInputFile) throws IOException {
        this.oldErasePin = null;
        this.newErasePin = null;
        parsePin(oldErasePinInString, newErasePinInString);
        loadDevices(drivesInputFile);
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
                    newErasePin, latch));
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
            for (KineticDevice device : succeed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("The following devices set erase pin failed:");
            for (KineticDevice device : failed) {
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
                byte[] newErasePin, CountDownLatch latch)
                throws KineticException {
            this.device = device;
            this.oldErasePin = oldErasePin;
            this.newErasePin = newErasePin;
            this.latch = latch;
            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(device.getInet4().get(0));
            adminClientConfig.setUseSsl(true);
            adminClientConfig.setPort(device.getTlsPort());
            adminClient = KineticAdminClientFactory
                    .createInstance(adminClientConfig);
        }

        @Override
        public void run() {
            try {
                adminClient.setErasePin(oldErasePin, newErasePin);
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
