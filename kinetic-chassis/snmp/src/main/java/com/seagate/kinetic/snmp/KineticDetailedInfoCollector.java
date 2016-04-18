package com.seagate.kinetic.snmp;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;

public class KineticDetailedInfoCollector {

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
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
                log = adminClient.getLog(listOfLogType);
                if (log != null) {
                    wwn = log.getConfiguration().getWorldWideName();
                }
            } catch (Throwable e) {
                adminClientConfig.setHost(kineticDevice.getIps().get(1));
                try {
                    adminClient = KineticAdminClientFactory
                            .createInstance(adminClientConfig);
                    log = adminClient.getLog(listOfLogType);
                    if (log != null) {
                        wwn = log.getConfiguration().getWorldWideName();
                    }
                } catch (Throwable e1) {
                }
            } finally {
                kineticDevice.setWwn(wwn);
            }
        }
    }

}
