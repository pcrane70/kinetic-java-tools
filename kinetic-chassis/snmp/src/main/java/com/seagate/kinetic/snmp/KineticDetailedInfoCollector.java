package com.seagate.kinetic.snmp;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.Interface;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;

public class KineticDetailedInfoCollector {
    private static final String DEFAULT_UNKNOWN_IP_ADDRESS = "0.0.0.0";

    public static void fullfillWwn(List<KineticDevice> kineticDevices) {
        if (null == kineticDevices || kineticDevices.isEmpty()) {
            return;
        }

        AdminClientConfiguration adminClientConfig = null;
        KineticAdminClient adminClient = null;
        for (KineticDevice kineticDevice : kineticDevices) {
            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(kineticDevice.getIps().get(0));
            adminClientConfig.setPort(kineticDevice.getPort());
            adminClientConfig.setUseSsl(false);
            adminClientConfig.setRequestTimeoutMillis(3 * 1000);

            List<KineticLogType> listOfLogType = new ArrayList<KineticLogType>();
            listOfLogType.add(KineticLogType.CONFIGURATION);
            KineticLog log = null;
            String wwn = "unknown";
            List<String> ips = new ArrayList<String>();
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
                log = adminClient.getLog(listOfLogType);
                if (log != null) {
                    if (log.getConfiguration() != null) {
                        wwn = log.getConfiguration().getWorldWideName();
                        List<Interface> intefList = log.getConfiguration()
                                .getInterfaces();
                        if (intefList != null && intefList.size() != 0) {
                            for (Interface intef : intefList) {
                                String ip = intef.getIpv4Address();
                                if (ip == null || ip.isEmpty()) {
                                    ips.add(DEFAULT_UNKNOWN_IP_ADDRESS);
                                } else {
                                    ips.add(intef.getIpv4Address());
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                adminClientConfig.setHost(kineticDevice.getIps().get(1));
                try {
                    adminClient = KineticAdminClientFactory
                            .createInstance(adminClientConfig);
                    log = adminClient.getLog(listOfLogType);
                    if (log != null) {
                        if (log.getConfiguration() != null) {
                            wwn = log.getConfiguration().getWorldWideName();
                            List<Interface> intefList = log.getConfiguration()
                                    .getInterfaces();
                            if (intefList != null && intefList.size() != 0) {
                                for (Interface intef : intefList) {
                                    String ip = intef.getIpv4Address();
                                    if (ip == null || ip.isEmpty()) {
                                        ips.add(DEFAULT_UNKNOWN_IP_ADDRESS);
                                    } else {
                                        ips.add(intef.getIpv4Address());
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable e1) {
                    System.out.println(e1.getMessage());
                }
            } finally {
                kineticDevice.setWwn(wwn);
            }
        }
    }

}
