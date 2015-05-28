package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.Capacity;
import kinetic.admin.Configuration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;
import kinetic.admin.Limits;
import kinetic.admin.Statistics;
import kinetic.admin.Temperature;
import kinetic.admin.Utilization;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.seagate.kinetic.tools.management.cli.impl.util.JsonUtil;

public class LogGetter extends DefaultExecuter {
    private static final String ALL = "all";
    private static final String TEMPERATURE = "temperature";
    private static final String CAPACITY = "capacity";
    private static final String UTILIZATION = "utilization";
    private static final String CONFIGURATION = "configuration";
    private static final String MESSAGES = "message";
    private static final String STATISTICS = "statistic";
    private static final String LIMITS = "limits";
    private static final int BATCH_THREAD_NUMBER = 20;
    private final Logger logger = Logger.getLogger(LogGetter.class.getName());
    private String logOutFile;
    private String logType;
    private StringBuffer sb;

    public LogGetter(String nodesLogFile, String logOutFile, String logType,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        this.logType = logType;
        this.logOutFile = logOutFile;
        this.sb = new StringBuffer();
        loadDevices(nodesLogFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
    }

    public void getAndStoreLog() throws Exception {

        ExecutorService pool = Executors.newCachedThreadPool();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        int batchTime = devices.size() / BATCH_THREAD_NUMBER;
        int restIpCount = devices.size() % BATCH_THREAD_NUMBER;

        System.out.println("Start getting and storing log......");

        sb.append("[\n");

        for (int i = 0; i < batchTime; i++) {
            CountDownLatch latch = new CountDownLatch(BATCH_THREAD_NUMBER);
            for (int j = 0; j < BATCH_THREAD_NUMBER; j++) {
                int num = i * BATCH_THREAD_NUMBER + j;
                pool.execute(new GetLogThread(logType, devices.get(num), latch,
                        useSsl, identity, key, clusterVersion, requestTimeout));
            }

            latch.await();
        }

        CountDownLatch latchRest = new CountDownLatch(restIpCount);
        for (int i = 0; i < restIpCount; i++) {
            int num = batchTime * BATCH_THREAD_NUMBER + i;

            pool.execute(new GetLogThread(logType, devices.get(num), latchRest,
                    useSsl, identity, key, clusterVersion, requestTimeout));
        }

        latchRest.await();

        pool.shutdown();

        sb.append("\n]");

        int totalDevices = devices.size();
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();

        assert (failedDevices + succeedDevices == totalDevices);

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();

        if (succeedDevices > 0) {
            System.out.println("\nThe following devices get log succeed:");
            for (KineticDevice dev : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(dev));
            }
        }

        if (failedDevices > 0) {
            System.out.println("\nThe following devices get log failed:");
            for (KineticDevice dev : failed.keySet()) {
                System.out.println(KineticDevice.toJson(dev));
            }
        }
        
        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")\n");

        persistToFile(sb.toString());
        System.out.println("Save logs to " + logOutFile + " completed.");
    }

    private void persistToFile(String log) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(logOutFile));
        fos.write(log.getBytes("UTF-8"));
        fos.flush();
        fos.close();
    }

    private String logToJson(KineticDevice device, KineticLog log,
            String logType) throws JsonGenerationException,
            JsonMappingException, IOException, KineticException {
        StringBuffer sb = new StringBuffer();
        sb.append("  {\n");
        sb.append("    \"device\": ");
        sb.append(JsonUtil.toJson(device));
        sb.append(",\n");
        sb.append("    \"log\": ");
        if (logType.equalsIgnoreCase(ALL)) {
            MyKineticLog myLog = new MyKineticLog(log);
            sb.append(JsonUtil.toJson(myLog));
        } else if (logType.equalsIgnoreCase(UTILIZATION)) {
            sb.append(JsonUtil.toJson(log.getUtilization()));
        } else if (logType.equalsIgnoreCase(CAPACITY)) {
            sb.append(JsonUtil.toJson(log.getCapacity()));
        } else if (logType.equalsIgnoreCase(TEMPERATURE)) {
            sb.append(JsonUtil.toJson(log.getTemperature()));
        } else if (logType.equalsIgnoreCase(CONFIGURATION)) {
            sb.append(JsonUtil.toJson(log.getConfiguration()));
        } else if (logType.equalsIgnoreCase(MESSAGES)) {
            sb.append("{\"messages\":");
            sb.append("\"");
            sb.append(new String(log.getMessages()));
            sb.append("\"}");
        } else if (logType.equalsIgnoreCase(STATISTICS)) {
            sb.append(JsonUtil.toJson(log.getStatistics()));
        } else if (logType.equalsIgnoreCase(LIMITS)) {
            sb.append(JsonUtil.toJson(log.getLimits()));
        } else {
            throw new IllegalArgumentException(
                    "Type should be utilization, capacity, temperature, configuration, message, statistic, limits or all");
        }
        sb.append("\n  }");

        return sb.toString();
    }

    private void validateLogType(String logType)
            throws IllegalArgumentException {
        if (logType == null || logType.isEmpty()) {
            throw new IllegalArgumentException("Type can not be empty");
        }

        if (!logType.equalsIgnoreCase(CAPACITY)
                && !logType.equalsIgnoreCase(TEMPERATURE)
                && !logType.equalsIgnoreCase(UTILIZATION)
                && !logType.equalsIgnoreCase(CONFIGURATION)
                && !logType.equalsIgnoreCase(MESSAGES)
                && !logType.equalsIgnoreCase(STATISTICS)
                && !logType.equalsIgnoreCase(LIMITS)
                && !logType.equalsIgnoreCase(ALL)) {
            throw new IllegalArgumentException(
                    "Type should be utilization, capacity, temperature, configuration, message, statistic, limits or all");
        }
    }

    private KineticLog getLog(KineticAdminClient kineticAdminClient,
            String logType) throws KineticException {
        validateLogType(logType);

        List<KineticLogType> listOfLogType = new ArrayList<KineticLogType>();

        if (logType.equalsIgnoreCase(ALL)) {
            return kineticAdminClient.getLog();
        } else if (logType.equalsIgnoreCase(UTILIZATION)) {
            listOfLogType.add(KineticLogType.UTILIZATIONS);
        } else if (logType.equalsIgnoreCase(CAPACITY)) {
            listOfLogType.add(KineticLogType.CAPACITIES);
        } else if (logType.equalsIgnoreCase(TEMPERATURE)) {
            listOfLogType.add(KineticLogType.TEMPERATURES);
        } else if (logType.equalsIgnoreCase(CONFIGURATION)) {
            listOfLogType.add(KineticLogType.CONFIGURATION);
        } else if (logType.equalsIgnoreCase(MESSAGES)) {
            listOfLogType.add(KineticLogType.MESSAGES);
        } else if (logType.equalsIgnoreCase(STATISTICS)) {
            listOfLogType.add(KineticLogType.STATISTICS);
        } else if (logType.equalsIgnoreCase(LIMITS)) {
            listOfLogType.add(KineticLogType.LIMITS);
        } else {
            throw new IllegalArgumentException(
                    "Type should be utilization, capacity, temperature, configuration, message, statistic, limits or all");
        }

        return kineticAdminClient.getLog(listOfLogType);
    }

    class GetLogThread implements Runnable {
        private AdminClientConfiguration adminClientConfig;
        private KineticAdminClient adminClient;
        private KineticDevice device;
        private CountDownLatch latch;

        public GetLogThread(String logType, KineticDevice device,
                CountDownLatch latch, boolean useSsl, long identity,
                String key, long clusterVersion, long requestTimeout)
                throws KineticException {
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
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
                KineticLog log = getLog(adminClient, logType);
                String log2String = logToJson(device, log, logType);

                synchronized (this) {
                    sb.append(log2String);
                    succeed.put(device, log2String);
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

    class MyKineticLog {
        private List<Utilization> utilization;
        private List<Temperature> temperature;
        private Capacity capacity;
        private Configuration configuration;
        private List<Statistics> statistics;
        private String messages;
        private KineticLogType[] containedLogTypes;
        private Limits limits;

        public MyKineticLog(KineticLog log) throws KineticException {
            this.utilization = log.getUtilization();
            this.temperature = log.getTemperature();
            this.capacity = log.getCapacity();
            this.configuration = log.getConfiguration();
            this.statistics = log.getStatistics();
            this.messages = new String(log.getMessages());
            this.containedLogTypes = log.getContainedLogTypes();
            this.limits = log.getLimits();
        }

        public List<Utilization> getUtilization() {
            return utilization;
        }

        public List<Temperature> getTemperature() {
            return temperature;
        }

        public Capacity getCapacity() {
            return capacity;
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public List<Statistics> getStatistics() {
            return statistics;
        }

        public String getMessages() {
            return messages;
        }

        public KineticLogType[] getContainedLogTypes() {
            return containedLogTypes;
        }

        public Limits getLimits() {
            return limits;
        }
    }
}
