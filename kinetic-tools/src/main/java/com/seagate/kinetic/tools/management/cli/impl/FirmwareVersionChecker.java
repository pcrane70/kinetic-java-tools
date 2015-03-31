package com.seagate.kinetic.tools.management.cli.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.admin.KineticLog;
import kinetic.admin.KineticLogType;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public class FirmwareVersionChecker extends DefaultExecuter {
    private String expectedFirewareVersion;

    public FirmwareVersionChecker(String expectedFirewareVersion,
            String nodesLogFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        this.expectedFirewareVersion = expectedFirewareVersion;
        loadDevices(nodesLogFile);
        initBasicSettings(useSsl, clusterVersion, identity, key, requestTimeout);
    }

    public void checkFirmwareVersion() throws JsonGenerationException,
            JsonMappingException, IOException {
        System.out.println("Start checking firmware version......");

        String firmwareVersion;
        for (KineticDevice device : devices) {
            try {
                firmwareVersion = getFirmwareVersion(device, useSsl,
                        clusterVersion, identity, key, requestTimeout);
                if (firmwareVersion.equals(expectedFirewareVersion)) {
                    System.out.println("[Passed]"
                            + KineticDevice.toJson(device));
                    succeed.put(device, firmwareVersion);
                } else {
                    System.out.println("[Failed]"
                            + KineticDevice.toJson(device));
                    failed.put(device, firmwareVersion);
                }
            } catch (KineticException e) {
                failed.put(device, "unknown");
                System.out.println("[Failed]" + KineticDevice.toJson(device));
                e.printStackTrace();
            }

        }

        int totalDevices = devices.size();
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();

        assert (failedDevices + succeedDevices == totalDevices);

        System.out.flush();
        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")");

        if (succeedDevices > 0) {
            System.out
                    .println("The following devices have same firmware version as expected:");
            for (KineticDevice device : succeed.keySet()) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out
                    .println("The following devices have different firmware version as expected (unknown means failing to get the firmware version):");
            for (KineticDevice device : failed.keySet()) {
                System.out.println("[" + failed.get(device) + "]"
                        + KineticDevice.toJson(device));
            }
        }
    }

    private String getFirmwareVersion(KineticDevice device, boolean useSsl,
            long clusterVersion, long identity, String key, long requestTimeout)
            throws KineticException {
        KineticAdminClient adminClient = null;
        AdminClientConfiguration adminClientConfig = null;

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

        adminClient = KineticAdminClientFactory
                .createInstance(adminClientConfig);

        List<KineticLogType> listOfLogType = new ArrayList<KineticLogType>();
        listOfLogType.add(KineticLogType.CONFIGURATION);
        KineticLog kineticLog = adminClient.getLog(listOfLogType);

        String version = kineticLog.getConfiguration().getVersion();

        try {
            adminClient.close();
        } catch (KineticException e) {
            e.printStackTrace();
        }

        return version;
    }
}
