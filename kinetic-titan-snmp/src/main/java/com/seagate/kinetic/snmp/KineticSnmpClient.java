package com.seagate.kinetic.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogLevel;
import org.snmp4j.smi.OID;

import com.google.gson.Gson;
import com.seagate.kinetic.snmp.core.SimpleSnmpClient;

public class KineticSnmpClient {
    private final static String PORT = System.getProperty("PORT", "8123");
    private final static String TLS_PORT = System.getProperty("TLS_PORT",
            "8443");
    private final static boolean DEBUG_FLAG = Boolean.parseBoolean(System
            .getProperty("DEBUG", "false"));
    
    private OID interfacesTableA = null;
    private OID interfacesTableB = null;
    private int startIndex;
    private int driveCount;
    private SimpleSnmpClient client;

    static {
        if (DEBUG_FLAG) {
            LogFactory.setLogFactory(new Log4jLogFactory());
            BasicConfigurator.configure();
            LogFactory.getLogFactory().getRootLogger()
                    .setLogLevel(LogLevel.ALL);
        }
    }

    public KineticSnmpClient(String address, String user, String password,
            String interfaceTableOidA, String interfaceTableOidB,
            int startIndex, int driveCount) {
        client = new SimpleSnmpClient(address, user, password);
        interfacesTableA = new OID(interfaceTableOidA);
        interfacesTableB = new OID(interfaceTableOidB);
        this.startIndex = startIndex;
        this.driveCount = driveCount;

    }

    public int countOfKineticDevices() throws IOException {
        List<String> ipAddressOfList = new ArrayList<String>();
        for (int i = startIndex; i < startIndex + driveCount; i++) {
            String ipAddressA = client.getAsString(new OID(interfacesTableA
                    .toString() + "." + String.valueOf(i)));
            if (!ipAddressA.equalsIgnoreCase("0.0.0.0")) {
                ipAddressOfList.add(ipAddressA);
            }
        }
        return ipAddressOfList.size();
    }

    @SuppressWarnings("unused")
    public List<KineticDevice> getKineticDevices() throws IOException {
        List<KineticDevice> kineticDevices = new ArrayList<KineticDevice>();
        String ipA, portA, tlsPortA, ipB, portB, tlsPortB;
        String ipAndPort;
        String temp[];
        for (int i = startIndex; i < startIndex + driveCount; i++) {
            KineticDevice kineticDevice = new KineticDevice();

            ipAndPort = client.getAsString(new OID(interfacesTableA.toString()
                    + "." + String.valueOf(i)));
            temp = ipAndPort.split(":");
            ipA = temp.length >= 1 ? temp[0] : "0.0.0.0";
            portA = temp.length >= 2 ? temp[1] : PORT;
            tlsPortA = temp.length >= 3 ? temp[2] : TLS_PORT;

            ipAndPort = client.getAsString(new OID(interfacesTableB.toString()
                    + "." + String.valueOf(i)));
            temp = ipAndPort.split(":");
            ipB = temp.length >= 1 ? temp[0] : "0.0.0.0";
            portB = temp.length >= 2 ? temp[1] : PORT;
            tlsPortB = temp.length >= 3 ? temp[2] : TLS_PORT;

            kineticDevice.setIps(ipA, ipB);
            kineticDevice.setPort(Integer.parseInt(portA));
            kineticDevice.setTlsPort(Integer.parseInt(tlsPortA));

            kineticDevices.add(kineticDevice);
        }

        return kineticDevices;
    }

    public static void main(String args[]) throws IOException {
        KineticSnmpClient kineticSnmpClient = new KineticSnmpClient(
                "udp:127.0.0.1/2001", "snmp", "password",
                "1.3.6.1.4.1.3581.12.7.2.1.1.10",
                "1.3.6.1.4.1.3581.12.7.2.1.1.11", 12, 84);

        System.out.println("DevicesCount: "
                + kineticSnmpClient.countOfKineticDevices());
        System.out.println("Devices: "
                + new Gson().toJson(kineticSnmpClient.getKineticDevices()));
    }
}
