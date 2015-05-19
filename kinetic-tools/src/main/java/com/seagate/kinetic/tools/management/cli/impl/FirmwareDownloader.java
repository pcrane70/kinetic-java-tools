package com.seagate.kinetic.tools.management.cli.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class FirmwareDownloader extends DefaultExecuter {
    private static final int CHUNK_SIZE = 1024;
    private static final int BATCH_THREAD_NUMBER = 20;
    private final Logger logger = Logger.getLogger(FirmwareDownloader.class
            .getName());
    private String firmware;
    private byte[] firmwareContent;

    public FirmwareDownloader(String firmware, String nodesLogFile,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        this.firmware = firmware;
        loadFirmware();
        loadDevices(nodesLogFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
    }

    private void loadFirmware() throws IOException {
        InputStream is = new FileInputStream(firmware);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[CHUNK_SIZE];
        int n;
        while ((n = is.read(b)) != -1) {
            out.write(b, 0, n);
        }
        is.close();
        firmwareContent = out.toByteArray();
    }

    public void updateFirmware() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        System.out.println("Start download firmware......");

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                pool.execute(new FirmwareDownloadThread(firmwareContent, latch,
                        devices.get(num), useSsl, clusterVersion, identity,
                        key, requestTimeout));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;

            pool.execute(new FirmwareDownloadThread(firmwareContent, latchRest,
                    devices.get(num), useSsl, clusterVersion, identity, key,
                    requestTimeout));
        }

        latchRest.await();

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
            System.out.println("Below devices downloaded firmware succeed:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out
                    .println("The following devices downloaded firmware failed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
    }

    class FirmwareDownloadThread implements Runnable {
        private KineticDevice device = null;
        private KineticAdminClient adminClient = null;
        private AdminClientConfiguration adminClientConfig = null;
        private byte[] firmwareContent = null;
        private CountDownLatch latch = null;

        public FirmwareDownloadThread(byte[] firmwareContent,
                CountDownLatch latch, KineticDevice device, boolean useSsl,
                long clusterVersion, long identity, String key,
                long requestTimeout) throws KineticException {
            this.device = device;
            this.firmwareContent = firmwareContent;
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

                adminClient.firmwareDownload(firmwareContent);

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
