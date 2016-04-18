package com.seagate.kinetic.snmp;

import java.util.ArrayList;
import java.util.List;

public class SnmpHwView {
    public List<SnmpHwViewRack> racks;

    public static SnmpHwView loadFromSnmpAgents(String agentIps,
            String agentPort, String SysDescOid, String interfaceTableOid,
            String rackCoordinate) {
        SnmpHwView snmpHwView = new SnmpHwView();
        KineticSnmpClient kineticSnmpClient = null;
        String agentAddress = null;
        List<KineticDevice> kineticDevices = null;

        snmpHwView.racks = new ArrayList<SnmpHwViewRack>();
        SnmpHwViewRack snmpHwViewRack = new SnmpHwViewRack();
        snmpHwView.racks.add(snmpHwViewRack);

        snmpHwViewRack.id = "1";
        snmpHwViewRack.coordinate = new Coordinate();
        String rackCoordinateInArray[] = rackCoordinate.split(",");
        snmpHwViewRack.coordinate.x = rackCoordinateInArray[0];
        snmpHwViewRack.coordinate.y = rackCoordinateInArray[1];
        snmpHwViewRack.coordinate.z = rackCoordinateInArray[2];
        snmpHwViewRack.chassis = new ArrayList<SnmpHwViewChassis>();

        SnmpHwViewChassis snmpHwViewChassis = null;
        String agentIpList[] = agentIps.split(",");
        int i = 0;
        for (String agentIp : agentIpList) {
            agentAddress = "udp:" + agentIp + "/" + agentPort;
            kineticSnmpClient = new KineticSnmpClient(agentAddress, SysDescOid,
                    interfaceTableOid);
            try {
                kineticDevices = kineticSnmpClient.getKineticDevices();
                KineticDetailedInfoCollector.fullfillWwn(kineticDevices);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
                continue;
            }

            snmpHwViewChassis = new SnmpHwViewChassis();
            i++;
            snmpHwViewChassis.id = i + "";
            snmpHwViewChassis.ips = new ArrayList<String>();
            snmpHwViewChassis.ips.add(agentIp);
            snmpHwViewChassis.ips.add(agentIp);

            snmpHwViewChassis.coordinate = new Coordinate();
            snmpHwViewChassis.coordinate.x = i + "";
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
                snmpHwViewChassis.coordinate.x = j + "";
                snmpHwViewChassis.coordinate.y = "";
                snmpHwViewChassis.coordinate.z = "";

                snmpHwViewChassis.devices.add(snmpHwViewDevice);
            }

            snmpHwViewRack.chassis.add(snmpHwViewChassis);
        }

        return snmpHwView;
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