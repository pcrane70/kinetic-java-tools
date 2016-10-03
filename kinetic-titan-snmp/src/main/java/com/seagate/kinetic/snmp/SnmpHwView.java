package com.seagate.kinetic.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class SnmpHwView {
    public static SnmpHwViewChassis loadFromSnmpAgents(String agentIp,
            String agentPort, String user, String password, String ipAOid,
            String ipBOid, String chassisId, int index, int count) {
        KineticSnmpClient kineticSnmpClient = null;
        String agentAddress = null;
        List<KineticDevice> kineticDevices = null;

        SnmpHwViewChassis snmpHwViewChassis = null;
        agentAddress = "udp:" + agentIp + "/" + agentPort;
        kineticSnmpClient = new KineticSnmpClient(agentAddress, user, password,
                ipAOid, ipBOid, index, count);
        try {
            kineticDevices = kineticSnmpClient.getKineticDevices();
            KineticDetailedInfoCollector.fullfillWwn(kineticDevices);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        snmpHwViewChassis = new SnmpHwViewChassis();
        snmpHwViewChassis.id = chassisId;
        snmpHwViewChassis.ips = new ArrayList<String>();
        snmpHwViewChassis.ips.add(agentIp);
        snmpHwViewChassis.ips.add(agentIp);

        snmpHwViewChassis.coordinate = new Coordinate();
        snmpHwViewChassis.coordinate.x = chassisId;
        snmpHwViewChassis.coordinate.y = "";
        snmpHwViewChassis.coordinate.z = "";

        snmpHwViewChassis.devices = new ArrayList<SnmpHwViewDevice>();
        SnmpHwViewDevice snmpHwViewDevice = null;
        int j = 0;
        for (KineticDevice kineticDevice : kineticDevices) {
            snmpHwViewDevice = new SnmpHwViewDevice();
            snmpHwViewDevice.deviceId = new SnmpHwViewChassisDeviceId();
            snmpHwViewDevice.deviceId.ips = kineticDevice.getIps();
            snmpHwViewDevice.deviceId.port = kineticDevice.getPort();
            snmpHwViewDevice.deviceId.tlsPort = kineticDevice.getTlsPort();
            snmpHwViewDevice.deviceId.wwn = kineticDevice.getWwn();
            snmpHwViewDevice.coordinate = new Coordinate();

            j++;
            snmpHwViewDevice.coordinate.x = j + "";
            snmpHwViewDevice.coordinate.y = "";
            snmpHwViewDevice.coordinate.z = "";

            snmpHwViewChassis.devices.add(snmpHwViewDevice);
        }

        return snmpHwViewChassis;
    }

    private static String readFile(String fileName) throws IOException {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        reader = new BufferedReader(new FileReader(new File(fileName)));
        String tempString = null;
        while ((tempString = reader.readLine()) != null) {
            sb.append(tempString);
        }
        reader.close();

        return sb.toString();
    }

    public static SnmpHwViewRack combineChassisToRack(String rackId,
            String rackCoordinate, String[] chassisFiles) throws Exception {
        if (chassisFiles == null || chassisFiles.length == 0) {
            throw new Exception("chassis json files are null or empty");
        }

        Gson gson = new Gson();
        SnmpHwViewRack snmpHwViewRack = new SnmpHwViewRack();
        snmpHwViewRack.chassis = new ArrayList<SnmpHwViewChassis>();

        snmpHwViewRack.coordinate = new Coordinate();
        String rackCoordinateInArray[] = rackCoordinate.split(",");
        snmpHwViewRack.coordinate.x = rackCoordinateInArray[0];
        snmpHwViewRack.coordinate.y = rackCoordinateInArray[1];
        snmpHwViewRack.coordinate.z = rackCoordinateInArray[2];

        snmpHwViewRack.id = rackId;

        SnmpHwViewChassis chassis = null;

        for (String chassisFile : chassisFiles) {
            chassis = gson.fromJson(readFile(chassisFile),
                    SnmpHwViewChassis.class);
            snmpHwViewRack.chassis.add(chassis);
        }

        return snmpHwViewRack;
    }

    public static List<SnmpHwViewRack> combineRacksToHWView(String[] rackFiles)
            throws Exception {
        if (rackFiles == null || rackFiles.length == 0) {
            throw new Exception("rack json files are null or empty");
        }

        Gson gson = new Gson();
        List<SnmpHwViewRack> racks = new ArrayList<SnmpHwViewRack>();
        SnmpHwViewRack rack = null;
        for (String rackFile : rackFiles) {
            rack = gson.fromJson(readFile(rackFile), SnmpHwViewRack.class);
            racks.add(rack);
        }

        return racks;
    }
}

class SnmpHwViewRack {
    public String id;
    public Coordinate coordinate;
    public List<SnmpHwViewChassis> chassis;
}

class SnmpHwViewChassis {
    public String id;
    public List<String> ips;
    public Coordinate coordinate;
    public List<SnmpHwViewDevice> devices;
}

class SnmpHwViewDevice {
    public SnmpHwViewChassisDeviceId deviceId;
    public Coordinate coordinate;
}

class SnmpHwViewChassisDeviceId {
    public List<String> ips;
    public int port;
    public int tlsPort;
    public String wwn;
}

class Coordinate {
    public String x = "";
    public String y = "";
    public String z = "";
}