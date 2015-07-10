/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kinetic.admin.Capacity;
import kinetic.admin.Configuration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;
import kinetic.admin.Limits;
import kinetic.admin.Statistics;
import kinetic.admin.Temperature;
import kinetic.admin.Utilization;
import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.common.util.MessageUtil;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.getlog.DeviceLog;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogResponse;

public class GetLog extends AbstractCommand {
    private static final String ALL = "all";
    private static final String TEMPERATURES = "temperatures";
    private static final String CAPACITIES = "capacities";
    private static final String UTILIZATIONS = "utilizations";
    private static final String CONFIGURATIONS = "configurations";
    private static final String MESSAGES = "messages";
    private static final String STATISTICS = "statistics";
    private static final String LIMITS = "limits";
    private static final String TEMPERATURE = "temperature";
    private static final String CAPACITY = "capacity";
    private static final String UTILIZATION = "utilization";
    private static final String CONFIGURATION = "configuration";
    private static final String MESSAGE = "message";
    private static final String STATISTIC = "statistic";
    private static final String LIMIT = "limit";
    private String logOutFile;
    private String logType;
    private KineticLogType kineticLogType;

    public GetLog(String nodesLogFile, String logOutFile, String logType,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                nodesLogFile);
        this.logType = logType;
        this.logOutFile = logOutFile;
    }

    public GetLog(List<DeviceId> deviceIds, String logOutFile, String logType,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, deviceIds);
        this.logType = logType;
        this.logOutFile = logOutFile;
    }

    private void getLogFromDevices() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new GetLogThread(logType, device));
        }
        poolExecuteThreadsInGroups(threads);
    }

    private String logToJson(KineticDevice device, KineticLog log,
            String logType) throws KineticException {
        StringBuffer sb = new StringBuffer();
        String jsonLog = "";
        sb.append("  {\n");
        sb.append("    \"device\": ");
        sb.append(MessageUtil.toJson(device));
        sb.append(",\n");
        sb.append("    \"log\": ");
        if (logType.equalsIgnoreCase(ALL)) {
            kineticLogType = null;
            MyKineticLog myLog = new MyKineticLog(log);
            jsonLog = MessageUtil.toJson(myLog);
        } else if (logType.equalsIgnoreCase(UTILIZATIONS)
                || logType.equalsIgnoreCase(UTILIZATION)) {
            kineticLogType = KineticLogType.UTILIZATIONS;
            jsonLog = MessageUtil.toJson(log.getUtilization());
        } else if (logType.equalsIgnoreCase(CAPACITIES)
                || logType.equalsIgnoreCase(CAPACITY)) {
            kineticLogType = KineticLogType.CAPACITIES;
            jsonLog = MessageUtil.toJson(log.getCapacity());
        } else if (logType.equalsIgnoreCase(TEMPERATURES)
                || logType.equalsIgnoreCase(TEMPERATURE)) {
            kineticLogType = KineticLogType.TEMPERATURES;
            jsonLog = MessageUtil.toJson(log.getTemperature());
        } else if (logType.equalsIgnoreCase(CONFIGURATIONS)
                || logType.equalsIgnoreCase(CONFIGURATION)) {
            kineticLogType = KineticLogType.CONFIGURATION;
            jsonLog = MessageUtil.toJson(log.getConfiguration());
        } else if (logType.equalsIgnoreCase(MESSAGES)
                || logType.equalsIgnoreCase(MESSAGE)) {
            kineticLogType = KineticLogType.MESSAGES;
            jsonLog = new String(log.getMessages());
        } else if (logType.equalsIgnoreCase(STATISTICS)
                || logType.equalsIgnoreCase(STATISTIC)) {
            kineticLogType = KineticLogType.STATISTICS;
            sb.append(MessageUtil.toJson(log.getStatistics()));
        } else if (logType.equalsIgnoreCase(LIMITS)
                || logType.equalsIgnoreCase(LIMIT)) {
            kineticLogType = KineticLogType.LIMITS;
            sb.append(MessageUtil.toJson(log.getLimits()));
        } else {
            throw new IllegalArgumentException(
                    "Type should be utilization/utilizations, capacity/capacities, temperature/temperatures, configuration/configurations, message/messages, statistic/statistics, limit/limits or all");
        }

        if (logType.equalsIgnoreCase(MESSAGES)
                || logType.equalsIgnoreCase(MESSAGE)) {
            sb.append("{\"messages\":");
            sb.append("\"");
            sb.append(jsonLog);
            sb.append("\"}");
        } else {
            sb.append(jsonLog);
        }
        report.setAdditionMessage(device, log);
        sb.append("\n  }");

        return sb.toString();
    }

    private void validateLogType(String logType)
            throws IllegalArgumentException {
        if (logType == null || logType.isEmpty()) {
            throw new IllegalArgumentException("Type can not be empty");
        }

        if (!logType.equalsIgnoreCase(CAPACITIES)
                && !logType.equalsIgnoreCase(CAPACITY)
                && !logType.equalsIgnoreCase(TEMPERATURES)
                && !logType.equalsIgnoreCase(TEMPERATURE)
                && !logType.equalsIgnoreCase(UTILIZATIONS)
                && !logType.equalsIgnoreCase(UTILIZATION)
                && !logType.equalsIgnoreCase(CONFIGURATIONS)
                && !logType.equalsIgnoreCase(CONFIGURATION)
                && !logType.equalsIgnoreCase(MESSAGES)
                && !logType.equalsIgnoreCase(MESSAGE)
                && !logType.equalsIgnoreCase(STATISTICS)
                && !logType.equalsIgnoreCase(STATISTIC)
                && !logType.equalsIgnoreCase(LIMITS)
                && !logType.equalsIgnoreCase(LIMIT)
                && !logType.equalsIgnoreCase(ALL)) {
            throw new IllegalArgumentException(
                    "Type should be utilization/utilizations, capacity/capacities, temperature/temperatures, configuration/configurations, message/messages, statistic/statistics, limit/limits or all");
        }
    }

    private KineticLog getLog(KineticAdminClient kineticAdminClient,
            String logType) throws KineticException {
        validateLogType(logType);

        List<KineticLogType> listOfLogType = new ArrayList<KineticLogType>();

        if (logType.equalsIgnoreCase(ALL)) {
            return kineticAdminClient.getLog();
        } else if (logType.equalsIgnoreCase(UTILIZATIONS)
                || logType.equalsIgnoreCase(UTILIZATION)) {
            listOfLogType.add(KineticLogType.UTILIZATIONS);
        } else if (logType.equalsIgnoreCase(CAPACITIES)
                || logType.equalsIgnoreCase(CAPACITY)) {
            listOfLogType.add(KineticLogType.CAPACITIES);
        } else if (logType.equalsIgnoreCase(TEMPERATURES)
                || logType.equalsIgnoreCase(TEMPERATURE)) {
            listOfLogType.add(KineticLogType.TEMPERATURES);
        } else if (logType.equalsIgnoreCase(CONFIGURATIONS)
                || logType.equalsIgnoreCase(CONFIGURATION)) {
            listOfLogType.add(KineticLogType.CONFIGURATION);
        } else if (logType.equalsIgnoreCase(MESSAGES)
                || logType.equalsIgnoreCase(MESSAGE)) {
            listOfLogType.add(KineticLogType.MESSAGES);
        } else if (logType.equalsIgnoreCase(STATISTICS)
                || logType.equalsIgnoreCase(STATISTIC)) {
            listOfLogType.add(KineticLogType.STATISTICS);
        } else if (logType.equalsIgnoreCase(LIMITS)
                || logType.equalsIgnoreCase(LIMIT)) {
            listOfLogType.add(KineticLogType.LIMITS);
        } else {
            throw new IllegalArgumentException(
                    "Type should be utilization/utilizations, capacity/capacites, temperature/temperatures, configuration/configurations, message/messages, statistic/statistics, limit/limits or all");
        }

        return kineticAdminClient.getLog(listOfLogType);
    }

    class GetLogThread extends AbstractWorkThread {
        public GetLogThread(String logType, KineticDevice device)
                throws KineticException {
            super(device);
        }

        @Override
        void runTask() throws KineticToolsException {
            try {
                KineticLog log = getLog(adminClient, logType);
                String log2String = logToJson(device, log, logType);
                synchronized (sb) {
                    sb.append(log2String);
                }
                report.reportSuccess(device);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
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

    @Override
    public void execute() throws KineticToolsException {
        try {
            getLogFromDevices();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        GetLogResponse response = new GetLogResponse();
        List<DeviceLog> deviceLogs = new ArrayList<DeviceLog>();
        for (KineticDevice kineticDevice : report.getSucceedDevices()) {
            try {
                addToDeviceLog(report, deviceLogs, kineticDevice,
                        kineticLogType, HttpServletResponse.SC_OK);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }

        for (KineticDevice kineticDevice : report.getFailedDevices()) {
            try {
                addToDeviceLog(report, deviceLogs, kineticDevice,
                        kineticLogType,
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }

        response.setDeviceLogs(deviceLogs);
        try {
            String toolHome = System.getProperty("kinetic.tools.out", ".");
            String rootDir = toolHome + File.separator + "out" + File.separator
                    + logOutFile;
            report.persistReport(MessageUtil.toJson(response), rootDir);
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }

    private void addToDeviceLog(Report report, List<DeviceLog> deviceLogs,
            KineticDevice kineticDevice, KineticLogType type, int responseCode)
            throws KineticException {
        DeviceId device;
        DeviceStatus dstatus;
        DeviceLog deviceLog;
        KineticLog myKineticLog = (KineticLog) report
                .getAdditionMessage(kineticDevice);
        device = toDeviceId(kineticDevice);
        deviceLog = new DeviceLog();
        dstatus = new DeviceStatus();
        dstatus.setDevice(device);

        if (null != myKineticLog) {
            if (null == type) {
                setDefaultLogTypes(deviceLog, myKineticLog);
            } else {
                switch (type) {
                case UTILIZATIONS:
                    deviceLog.setUtilization(myKineticLog.getUtilization());
                    break;
                case TEMPERATURES:
                    deviceLog.setTemperature(myKineticLog.getTemperature());
                    break;
                case CAPACITIES:
                    deviceLog.setCapacity(myKineticLog.getCapacity());
                    break;
                case CONFIGURATION:
                    deviceLog.setConfiguration(myKineticLog.getConfiguration());
                    break;
                case STATISTICS:
                    deviceLog.setStatistics(myKineticLog.getStatistics());
                    break;
                case MESSAGES:
                    deviceLog.setMessages(myKineticLog.getMessages());
                    break;
                case LIMITS:
                    deviceLog.setLimits(myKineticLog.getLimits());
                    break;
                case DEVICE:
                    break;
                default:
                    setDefaultLogTypes(deviceLog, myKineticLog);
                }
            }
        }

        deviceLog.setDeviceStatus(dstatus);
        deviceLogs.add(deviceLog);
    }

    private void setDefaultLogTypes(DeviceLog deviceLog, KineticLog myKineticLog)
            throws KineticException {
        deviceLog.setCapacity(myKineticLog.getCapacity());
        deviceLog.setConfiguration(myKineticLog.getConfiguration());
        deviceLog.setLimits(myKineticLog.getLimits());
        deviceLog.setStatistics(myKineticLog.getStatistics());
        KineticLogType[] logTypes = myKineticLog.getContainedLogTypes();
        List<KineticLogType> listOfNewLogType = new ArrayList<KineticLogType>();
        for (int i = 0; i < logTypes.length; i++) {
            if (!logTypes[i].equals(KineticLogType.MESSAGES)) {
                listOfNewLogType.add(logTypes[i]);
            }
        }
        KineticLogType[] arrayOfNewLogType = new KineticLogType[listOfNewLogType
                .size()];
        deviceLog.setContainedLogTypes(listOfNewLogType
                .toArray(arrayOfNewLogType));
        deviceLog.setTemperature(myKineticLog.getTemperature());
        deviceLog.setUtilization(myKineticLog.getUtilization());
    }
}
