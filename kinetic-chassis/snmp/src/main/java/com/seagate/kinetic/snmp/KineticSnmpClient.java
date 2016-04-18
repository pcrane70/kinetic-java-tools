package com.seagate.kinetic.snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.smi.OID;

import com.google.gson.Gson;
import com.seagate.kinetic.snmp.core.SimpleSnmpClient;

public class KineticSnmpClient {
    private OID sysDescr = null;
    private OID interfacesTable = null;
    private SimpleSnmpClient client;
    private Gson gson = new Gson();

    public KineticSnmpClient(String address, String systemDescOid,
            String interfaceTableOid) {
        client = new SimpleSnmpClient(address);
        sysDescr = new OID(systemDescOid);
        interfacesTable = new OID(interfaceTableOid);
    }

    public String getSysDescr() throws IOException {
        return client.getAsString(sysDescr);
    }

    public int countOfKineticDevices() {
        List<List<String>> tableContents = client
                .getTableAsStrings(new OID[] { new OID(interfacesTable
                        .toString() + ".1") });
        return tableContents.size();
    }

    public List<KineticDevice> getKineticDevices() throws IOException {
        List<List<String>> tableContents = client
                .getTableAsStrings(new OID[] { new OID(interfacesTable
                        .toString() + ".1") });
        int size = tableContents.size();
        List<KineticDevice> kineticDevices = new ArrayList<KineticDevice>();
        KineticDevice kineticDevice = null;
        String temp = null;
        for (int i = 0; i < size; i++) {
            temp = tableContents.get(i).get(0);
            kineticDevice = gson.fromJson(temp, KineticDevice.class);
            kineticDevices.add(kineticDevice);
        }

        return kineticDevices;
    }

    public static void main(String args[]) throws IOException {
        KineticSnmpClient kineticSnmpClient = new KineticSnmpClient(
                "udp:127.0.0.1/2001", ".1.3.6.1.2.1.1.1.0",
                ".1.3.6.1.2.1.2.2.1");
        System.out.println("SystemDesc: " + kineticSnmpClient.getSysDescr());
        System.out.println("DevicesCount: "
                + kineticSnmpClient.countOfKineticDevices());
        System.out.println("Devices: "
                + new Gson().toJson(kineticSnmpClient.getKineticDevices()));
    }
}
