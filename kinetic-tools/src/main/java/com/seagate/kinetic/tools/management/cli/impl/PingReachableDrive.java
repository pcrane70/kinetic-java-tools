package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.ConnectionListener;
import kinetic.client.KineticException;

public class PingReachableDrive extends DefaultExecuter {
    private static final String SUBNET_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final int SUB_NET_LENGTH = 255;
    private static final int BATCH_THREAD_NUMBER = 20;
    private static final int TLS_PORT = 8443;
    private static final int PORT = 8123;
    private final Logger logger = Logger.getLogger(PingReachableDrive.class
            .getName());
    private String driveListOutputFile;
    private String subnetPrefix;

    public PingReachableDrive(String subnetPrefixOrDriveInputFilePath,
            String driveListOutputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);

        if (validateSubnet(subnetPrefixOrDriveInputFilePath)) {
            this.subnetPrefix = subnetPrefixOrDriveInputFilePath;
        } else {
            loadDevices(subnetPrefixOrDriveInputFilePath);
        }

        this.driveListOutputFile = driveListOutputFile;
    }

    public void pingReachableDriveViaSubnet() throws Exception {

        ExecutorService pool = Executors.newCachedThreadPool();
        UnSolicitedConnectionListener listener = new UnSolicitedConnectionListener();

        int batchTime = SUB_NET_LENGTH / BATCH_THREAD_NUMBER;
        int restIpCount = SUB_NET_LENGTH % BATCH_THREAD_NUMBER;

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                String ipAddress = subnetPrefix + "." + num;

                List<String> inet4 = new ArrayList<String>();
                inet4.add(ipAddress);

                KineticDevice device = new KineticDevice();
                device.setInet4(inet4);
                device.setPort(PORT);
                device.setTlsPort(TLS_PORT);

                pool.execute(new PingReachableDriveViaSubnetThread(latch,
                        device, useSsl, clusterVersion, identity, key,
                        requestTimeout, listener));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;
            String ipAddress = subnetPrefix + "." + num;

            List<String> inet4 = new ArrayList<String>();
            inet4.add(ipAddress);

            KineticDevice device = new KineticDevice();
            device.setInet4(inet4);
            device.setPort(PORT);
            device.setTlsPort(TLS_PORT);

            pool.execute(new PingReachableDriveViaSubnetThread(latchRest,
                    device, useSsl, clusterVersion, identity, key,
                    requestTimeout, listener));
        }

        latchRest.await();

        pool.shutdown();

        TimeUnit.SECONDS.sleep(1);

        Map<KineticDevice, String> devices = listener.getDevices();

        if (devices.size() > 0) {

            persistToFile(devices.keySet(), driveListOutputFile);

            for (KineticDevice device : devices.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }

            System.out.println("\nDiscovered " + devices.size()
                    + " drives via subnet: " + subnetPrefix
                    + ", persist drives info in " + driveListOutputFile);
        }
    }

    public void pingReachableDriveViaDriveList() throws Exception {

        ExecutorService pool = Executors.newCachedThreadPool();
        UnSolicitedConnectionListener listener = new UnSolicitedConnectionListener();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                KineticDevice device = devices.get(num);

                pool.execute(new PingReachableDriveViaSubnetThread(latch,
                        device, useSsl, clusterVersion, identity, key,
                        requestTimeout, listener));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;
            KineticDevice device = devices.get(num);

            pool.execute(new PingReachableDriveViaSubnetThread(latchRest,
                    device, useSsl, clusterVersion, identity, key,
                    requestTimeout, listener));
        }

        latchRest.await();

        pool.shutdown();

        TimeUnit.SECONDS.sleep(2);

        Map<KineticDevice, String> devicesofPingable = listener.getDevices();

        int totalDevices = devices.size();
        int failedDevices = failed.size();
        int succeedDevices = devicesofPingable.size();

        assert (failedDevices + succeedDevices == totalDevices);

        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")");

        if (failedDevices > 0) {
            System.out.println("\nThe following devices ping failed:");
            for (KineticDevice device : failed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (succeedDevices > 0) {
            persistToFile(devicesofPingable.keySet(), driveListOutputFile);

            System.out.println("\nThe following devices ping succeed:");
            for (KineticDevice device : devicesofPingable.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }

            System.out.println("\nDiscovered " + succeedDevices
                    + " drives online, persist drives info in "
                    + driveListOutputFile);
        }
    }

    private String persistToFile(Set<KineticDevice> deviceList, String filePath)
            throws Exception {
        assert (filePath != null);
        assert (deviceList != null);

        FileOutputStream fos = new FileOutputStream(new File(filePath));
        StringBuffer sb = new StringBuffer();
        for (KineticDevice device : deviceList) {
            sb.append(KineticDevice.toJson(device));
            sb.append("\n");
        }
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();

        return sb.toString();
    }

    private boolean validateSubnet(String subnet) {
        Pattern pattern = Pattern.compile(SUBNET_PATTERN);
        Matcher matcher = pattern.matcher(subnet);

        return matcher.matches();
    }

    class PingReachableDriveViaSubnetThread implements Runnable {
        private AdminClientConfiguration adminClientConfig;
        private KineticAdminClient adminClient = null;
        private CountDownLatch latch;
        private KineticDevice device;

        public PingReachableDriveViaSubnetThread(CountDownLatch latch,
                KineticDevice device, boolean useSsl, long clusterVersion,
                long identity, String key, long requestTimeout,
                ConnectionListener listener) throws KineticException {
            this.latch = latch;
            this.device = device;

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
            adminClientConfig.setRequestTimeoutMillis(requestTimeout);
            adminClientConfig.setUserId(identity);
            adminClientConfig.setKey(key);
            adminClientConfig.setConnectionListener(listener);
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
            } catch (KineticException e) {
                failed.put(device, "");

                logger.warning(e.getMessage());
            } catch (Exception e) {
                failed.put(device, "");

                logger.warning(e.getMessage());
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
