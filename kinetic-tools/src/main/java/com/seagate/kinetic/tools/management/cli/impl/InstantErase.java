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

public class InstantErase extends DeviceLoader{
    private byte[] erasePin;
    private List<KineticDevice> failed = new ArrayList<KineticDevice>();
    private List<KineticDevice> succeed = new ArrayList<KineticDevice>();

    public InstantErase(String erasePinInString, String drivesInputFile)
            throws IOException {
        this.erasePin = null;
        parsePin(erasePinInString);
        loadDevices(drivesInputFile);
    }

    private void parsePin(String erasePin) {
        if (null != erasePin) {
            this.erasePin = erasePin.getBytes(Charset.forName("UTF-8"));
        }
    }

    public void instantErase() throws InterruptedException, KineticException,
            IOException {
        CountDownLatch latch = new CountDownLatch(devices.size());
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("Start instant erase...");

        for (KineticDevice device : devices) {
            pool.execute(new instantEraseThread(device, erasePin, latch));
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
            System.out.println("The following devices instant erase succeed:");
            for (KineticDevice device : succeed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("The following devices instant erase failed:");
            for (KineticDevice device : failed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
    }

    class instantEraseThread implements Runnable {
        private KineticDevice device = null;
        private KineticAdminClient adminClient = null;
        private AdminClientConfiguration adminClientConfig = null;
        private byte[] erasePin = null;
        private CountDownLatch latch = null;

        public instantEraseThread(KineticDevice device, byte[] erasePin,
                CountDownLatch latch) throws KineticException {
            this.device = device;
            this.erasePin = erasePin;
            this.latch = latch;
            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(device.getInet4().get(0));
            adminClientConfig.setUseSsl(true);
            adminClientConfig.setPort(device.getTlsPort());
            adminClientConfig.setRequestTimeoutMillis(180000);
            adminClient = KineticAdminClientFactory
                    .createInstance(adminClientConfig);
        }

        @Override
        public void run() {
            try {
                adminClient.instantErase(erasePin);
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
