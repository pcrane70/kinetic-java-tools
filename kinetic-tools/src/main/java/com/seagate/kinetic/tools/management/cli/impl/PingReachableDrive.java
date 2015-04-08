package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.admin.KineticLogType;
import kinetic.client.ConnectionListener;
import kinetic.client.KineticException;

public class PingReachableDrive extends DefaultExecuter {
    private final Logger logger = Logger.getLogger(PingReachableDrive.class
            .getName());
    private static final int SUB_NET_LENGTH = 255;
    private static final int BATCH_THREAD_NUMBER = 100;
    private static final int TLS_PORT = 8443;
    private static final int PORT = 8123;
    private List<KineticLogType> logTypes = new ArrayList<KineticLogType>();
    private String driveListOutputFile;
    private String subnetPrefix;
    private boolean useSsl;
    private long clusterVersion;
    private long identity;
    private String key;
    private long requestTimeout;

    public PingReachableDrive(List<KineticDevice> devices, boolean useSsl,
            long clusterVersion, long identity, String key, long requestTimeout) {

        this.devices = devices;
        this.useSsl = useSsl;
        this.clusterVersion = clusterVersion;
        this.identity = identity;
        this.key = key;
        this.requestTimeout = requestTimeout;
    }

    public PingReachableDrive(String subnetPrefix, String driveListOutputFile,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) {
        this.subnetPrefix = subnetPrefix;
        this.driveListOutputFile = driveListOutputFile;
        this.useSsl = useSsl;
        this.clusterVersion = clusterVersion;
        this.identity = identity;
        this.key = key;
        this.requestTimeout = requestTimeout;

        logTypes.add(KineticLogType.CONFIGURATION);
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

                pool.execute(new PingReachableDriveViaSubnetThread(latch,
                        ipAddress, useSsl, clusterVersion, identity, key,
                        requestTimeout, listener));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;
            String ipAddress = subnetPrefix + "." + num;

            pool.execute(new PingReachableDriveViaSubnetThread(latchRest,
                    ipAddress, useSsl, clusterVersion, identity, key,
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

    class PingReachableDriveViaSubnetThread implements Runnable {
        private AdminClientConfiguration adminClientConfig;
        private KineticAdminClient adminClient = null;
        private CountDownLatch latch;

        public PingReachableDriveViaSubnetThread(CountDownLatch latch,
                String host, boolean useSsl, long clusterVersion,
                long identity, String key, long requestTimeout,
                ConnectionListener listener) {
            this.latch = latch;
            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(host);
            adminClientConfig.setUseSsl(useSsl);
            if (useSsl) {
                adminClientConfig.setPort(TLS_PORT);
            } else {
                adminClientConfig.setPort(PORT);
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
                logger.warning(e.getMessage());
            } catch (Exception e) {
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
