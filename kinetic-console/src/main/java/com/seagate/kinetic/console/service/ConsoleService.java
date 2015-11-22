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
package com.seagate.kinetic.console.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
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

import com.google.gson.Gson;
import com.seagate.kinetic.console.ConsoleConfiguration;
import com.seagate.kinetic.tools.management.rest.message.hwview.Chassis;
import com.seagate.kinetic.tools.management.rest.message.hwview.Device;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareView;
import com.seagate.kinetic.tools.management.rest.message.hwview.Rack;

public class ConsoleService {
    private static final Logger logger = Logger.getLogger(ConsoleService.class
            .getName());
    private static final int UNREACHABLE_STATE_FAILURE_TIMES = 2;
    private static final int FAILURE_STATE_FAILURE_TIMES = 30;
    private static final int HEARTBEAT_THREAD_DETECT_PERIOD_IN_SEC = 2;
    private HardwareView hwView;
    private Map<String, Integer> devicesStateMap;
    private Map<String, MyKineticLog> kineticLogMap;
    private boolean debugMode = false;
    private Gson gson = null;

    public ConsoleService() {
        this(null);
    }

    public ConsoleService(String hwvFile) {
        devicesStateMap = new ConcurrentHashMap<String, Integer>();
        kineticLogMap = new ConcurrentHashMap<String, MyKineticLog>();
        gson = new Gson();
        init(hwvFile);
    }

    public HardwareView getHardwareView() {
        return hwView;
    }

    private void init(String hwvFile) {
        hwView = readTemplet(hwvFile);
        for (Rack rack : hwView.getRacks()) {
            for (Chassis chassis : rack.getChassis()) {
                for (Device device : chassis.getDevices()) {
                    devicesStateMap.put(device.getDeviceId().getWwn(),
                            DeviceState.NORMAL);
                }

                // start heartbeat threads to monitor each chassis
                new HeartbeatThread(chassis).start();
            }
        }
    }

    public void enableDebugMode() {
        debugMode = true;
    }

    public void disableDebugMode() {
        debugMode = false;
    }

    public String listDevicesState() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        int i = 0;
        for (String key : devicesStateMap.keySet()) {
            sb.append("{\"wwn\": \"" + key + "\",");
            sb.append("\"state\": " + devicesStateMap.get(key) + "}");
            if (++i != devicesStateMap.size()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static List<String> listHwViewFiles() {
        String configDir = ConsoleConfiguration.getConsoleHome()
                + File.separator + "config" + File.separator + "default";
        File dir = new File(configDir);
        File hwviewFiles[] = dir.listFiles();

        List<String> files = new ArrayList<String>();
        for (File file : hwviewFiles) {
            files.add(file.getName());
        }

        return files;
    }

    public String describeDevice(String wwn) {
        StringBuffer sb = new StringBuffer();

        if (wwn == null || wwn.isEmpty()) {
            sb.append("[");
            int i = 0;
            for (String key : devicesStateMap.keySet()) {
                if (kineticLogMap.containsKey(key)) {
                    sb.append("{\"wwn\": \"" + key + "\",");
                    sb.append("\"state\": " + devicesStateMap.get(key) + ",");
                    sb.append("\"ready\": " + 0 + ",");
                    sb.append("\"log\": " + gson.toJson(kineticLogMap.get(key))
                            + "}");
                } else {
                    sb.append("{\"wwn\": \"" + key + "\",");
                    sb.append("\"state\": " + devicesStateMap.get(key) + ",");
                    sb.append("\"ready\": " + 1 + "}");
                }

                if (++i != devicesStateMap.size()) {
                    sb.append(",");
                }
            }
            sb.append("]");
        } else {
            if (kineticLogMap.containsKey(wwn)) {
                sb.append("{\"wwn\": " + wwn + "\",");
                sb.append("\"ready\": " + 0 + ",");
                sb.append("\"log\": " + gson.toJson(kineticLogMap.get(wwn))
                        + "}");
            } else {
                sb.append("{\"wwn\": " + wwn + "\",");
                sb.append("\"ready\": " + 1 + "}");
            }
        }

        // System.out.println(sb.toString());

        return sb.toString();
    }

    private HardwareView readTemplet(String hwvFile) {

        String path = null;
        if (hwvFile == null || hwvFile.isEmpty()) {
            path = ConsoleConfiguration.getHardwareConfigTemplet();
        } else {
            path = ConsoleConfiguration.getConsoleHome() + File.separator
                    + "config" + File.separator + "default" + File.separator
                    + hwvFile;
        }

        HardwareView view = null;

        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            view = gson.fromJson(br, HardwareView.class);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        return view;
    }

    class HeartbeatThread extends Thread {
        private Chassis chassis;
        private Map<String, KineticAdminClient> adminClientMap;
        private Map<String, Integer> failureCounterMap;
        private Random random = new Random();

        public HeartbeatThread(Chassis chassis) {
            this.chassis = chassis;
            this.adminClientMap = new HashMap<String, KineticAdminClient>();
            this.failureCounterMap = new HashMap<String, Integer>();
        }

        @Override
        public void run() {
            while (true) {
                for (Device device : chassis.getDevices()) {
                    KineticAdminClient adminClient = null;
                    String alternativeHost = null;
                    String wwn = device.getDeviceId().getWwn();
                    if (!debugMode) {
                        if ((adminClient = adminClientMap.get(device
                                .getDeviceId().getWwn())) == null) {
                            AdminClientConfiguration config = new AdminClientConfiguration();
                            config.setRequestTimeoutMillis(5000);
                            config.setUseSsl(false);
                            config.setHost(device.getDeviceId().getIps()[0]);
                            config.setPort(device.getDeviceId().getPort());
                            if (device.getDeviceId().getIps().length > 1) {
                                alternativeHost = device.getDeviceId().getIps()[1];
                            }
                            config.setPort(device.getDeviceId().getPort());
                            try {
                                adminClient = KineticAdminClientFactory
                                        .createInstance(config);
                                adminClientMap.put(wwn, adminClient);
                            } catch (KineticException e) {
                                config.setHost(alternativeHost);
                                try {
                                    adminClient = KineticAdminClientFactory
                                            .createInstance(config);
                                    adminClientMap.put(device.getDeviceId()
                                            .getWwn(), adminClient);
                                } catch (KineticException e1) {
                                    Integer fails = null;
                                    if ((fails = failureCounterMap.get(wwn)) == null) {
                                        fails = 1;
                                    } else {
                                        fails += 1;
                                    }

                                    failureCounterMap.put(wwn, fails);
                                    if (fails >= UNREACHABLE_STATE_FAILURE_TIMES
                                            && fails < FAILURE_STATE_FAILURE_TIMES) {
                                        devicesStateMap.put(wwn,
                                                DeviceState.UNREACHABLE);
                                    } else if (fails >= FAILURE_STATE_FAILURE_TIMES) {
                                        devicesStateMap.put(wwn,
                                                DeviceState.FAILURE);
                                    }
                                }
                            }
                        }

                        if (adminClient != null) {
                            List<KineticLogType> logTypeOfList = new ArrayList<KineticLogType>();
                            logTypeOfList.add(KineticLogType.CAPACITIES);
                            logTypeOfList.add(KineticLogType.TEMPERATURES);
                            logTypeOfList.add(KineticLogType.UTILIZATIONS);
                            logTypeOfList.add(KineticLogType.STATISTICS);
                            try {
                                KineticLog log = adminClient
                                        .getLog(logTypeOfList);
                                kineticLogMap.put(wwn, new MyKineticLog(log));
                                failureCounterMap.put(wwn, 0);
                                devicesStateMap.put(wwn, DeviceState.NORMAL);

                            } catch (KineticException e) {
                                Integer fails = null;
                                if ((fails = failureCounterMap.get(wwn)) == null) {
                                    fails = 1;
                                } else {
                                    fails += 1;
                                }

                                failureCounterMap.put(wwn, fails);
                                if (fails >= UNREACHABLE_STATE_FAILURE_TIMES
                                        && fails < FAILURE_STATE_FAILURE_TIMES) {
                                    devicesStateMap.put(wwn,
                                            DeviceState.UNREACHABLE);
                                } else if (fails >= FAILURE_STATE_FAILURE_TIMES) {
                                    devicesStateMap.put(wwn,
                                            DeviceState.FAILURE);
                                }

                                adminClientMap.remove(wwn);
                            }
                        }
                    } else {
                        devicesStateMap.put(wwn, random.nextInt(3));
                    }
                }

                try {
                    TimeUnit.SECONDS
                            .sleep(HEARTBEAT_THREAD_DETECT_PERIOD_IN_SEC);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // ConsoleService consoleService = new ConsoleService();
        System.out.println(ConsoleService.listHwViewFiles().toString());
        // consoleService.enableDebugMode();

        // while (true) {
        // TimeUnit.SECONDS.sleep(10);
        // System.out.println(consoleService.describeDevice(null));
        // }
    }
}

class MyKineticLog {
    private List<Utilization> utilization;
    private List<Temperature> temperature;
    private Capacity capacity;
    private Configuration configuration;
    private List<Statistics> statistics;
    private Limits limits;

    public MyKineticLog(KineticLog log) throws KineticException {
        this.utilization = log.getUtilization();
        this.temperature = log.getTemperature();
        this.capacity = log.getCapacity();
        this.configuration = log.getConfiguration();
        this.statistics = log.getStatistics();
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

    public Limits getLimits() {
        return limits;
    }
}
