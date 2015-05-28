package com.seagate.kinetic.tools.management.cli.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.Device;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.seagate.kinetic.tools.management.cli.impl.util.JsonUtil;

public class VendorSpecificDeviceLogGetter extends DefaultExecuter {
    private static final int BATCH_THREAD_NUMBER = 20;
    private final Logger logger = Logger
            .getLogger(VendorSpecificDeviceLogGetter.class.getName());
    private byte[] vendorSpecificName;
    private String outputFilePath;
    private StringBuffer sb = new StringBuffer();

    public VendorSpecificDeviceLogGetter(String vendorSpecificNameInString,
            String drivesInputFiles, String outputFilePath, boolean useSsl,
            long clusterVersion, long identity, String key, long requestTimeout)
            throws IOException {
        this.outputFilePath = outputFilePath;
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
        loadDevices(drivesInputFiles);
        this.vendorSpecificName = null;
        parseVendorSpecificDeviceName(vendorSpecificNameInString);
    }

    private void parseVendorSpecificDeviceName(String vendorSpecificNameInString) {
        if (vendorSpecificNameInString != null) {
            vendorSpecificName = vendorSpecificNameInString.getBytes(Charset
                    .forName("UTF-8"));
        }
    }

    public void vendorSpecificDeviceLogGetter() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                pool.execute(new getVendorSpecificDeviceLogThread(devices
                        .get(num), vendorSpecificName, latch, useSsl,
                        clusterVersion, identity, key, requestTimeout));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;

            pool.execute(new getVendorSpecificDeviceLogThread(devices.get(num),
                    vendorSpecificName, latchRest, useSsl, clusterVersion,
                    identity, key, requestTimeout));
        }

        latchRest.await();

        pool.shutdown();

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();

        int totalDevices = devices.size();
        int succeedDevices = succeed.size();
        int failedDevices = failed.size();

        assert (succeedDevices + failedDevices == totalDevices);

        if (succeedDevices > 0) {
            System.out
                    .println("\nThe following devices get vendor specific info succeed:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out
                    .println("\nThe following device get vendor specific info failed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")\n");

        persist2File(sb.toString());
        System.out.println("Save logs to " + outputFilePath + " completed.");
    }

    private String device2Json(KineticDevice kineticDevice, Device device)
            throws JsonGenerationException, JsonMappingException, IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(" {\n");
        sb.append("   \"device\":");
        sb.append(JsonUtil.toJson(kineticDevice));
        sb.append(",\n");

        sb.append("   \"vendorspecificname\":");
        sb.append(JsonUtil.toJson(new String(device.getName())));
        sb.append(",\n");

        sb.append("   \"vendorspecificvalue\":");
        sb.append(new String(device.getValue()));
        sb.append("\n }");

        return sb.toString();
    }

    private void persist2File(String sb) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFilePath);
        fos.write(sb.getBytes("UTF-8"));
        fos.flush();
        fos.close();
    }

    class getVendorSpecificDeviceLogThread implements Runnable {
        private byte[] vendorSpecificName;
        private CountDownLatch latch;
        private KineticDevice device;
        private AdminClientConfiguration adminClientConfig;
        private KineticAdminClient adminClient;

        public getVendorSpecificDeviceLogThread(KineticDevice device,
                byte[] vendorSpecificName, CountDownLatch latch,
                boolean useSsl, long clusterVersion, long identity, String key,
                long requestTimeout) throws KineticException {
            this.vendorSpecificName = vendorSpecificName;
            this.device = device;
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
                adminClientConfig.setThreadPoolAwaitTimeOut(1000);
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

                Device vendorSpecficInfo = adminClient
                        .getVendorSpecificDeviceLog(vendorSpecificName);
                String vendorSpecficInfo2Json = device2Json(device,
                        vendorSpecficInfo);

                synchronized (this) {
                    sb.append(vendorSpecficInfo2Json + "\n");
                    succeed.put(device, vendorSpecficInfo2Json);
                }

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
