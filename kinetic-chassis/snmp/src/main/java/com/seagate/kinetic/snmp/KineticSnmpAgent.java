package com.seagate.kinetic.snmp;

import java.io.IOException;
import java.util.List;

import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;

import com.google.gson.Gson;
import com.seagate.kinetic.snmp.conf.KineticSnmpAgentConfig;
import com.seagate.kinetic.snmp.core.Agent;
import com.seagate.kinetic.snmp.core.MOScalarFactory;
import com.seagate.kinetic.snmp.core.MOTableBuilder;

public class KineticSnmpAgent {
    private static final OID systemDescrption = new OID(
            KineticSnmpAgentConfig.getSystemDescriptionOid());
    private static final OID interfacesTable = new OID(
            KineticSnmpAgentConfig.getInterfaceTableOid());
    private Agent agent;
    private Gson gson = new Gson();

    public KineticSnmpAgent(String address, List<KineticDevice> kineticDevices)
            throws IOException {
        agent = new Agent(address);
        agent.start();

        setManagedObject(kineticDevices);
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void setManagedObject(List<KineticDevice> kineticDevices) {
        agent.unregisterManagedObject(agent.getSnmpv2MIB());
        agent.registerManagedObject(MOScalarFactory.createReadOnly(
                systemDescrption, KineticSnmpAgentConfig.getSystemDescription()));

        MOTableBuilder builder = new MOTableBuilder(interfacesTable);

        builder.addColumnType(SMIConstants.SYNTAX_OCTET_STRING,
                MOAccessImpl.ACCESS_READ_ONLY);

        for (KineticDevice device : kineticDevices) {
            builder.addRowValue(new OctetString(gson.toJson(device)));
        }

        agent.registerManagedObject(builder.build());
    }

    public static void main(String args[]) throws IOException {

        List<KineticDevice> kineticDevices = KineticSnmpAgentConfig
                .loadKineticDevices();
        new KineticSnmpAgent(KineticSnmpAgentConfig.getAgentAddress(),
                kineticDevices);
    }
}
