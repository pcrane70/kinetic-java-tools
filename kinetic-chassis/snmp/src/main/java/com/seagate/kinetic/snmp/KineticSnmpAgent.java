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
    private KineticSnmpAgentConfig config = null;
    private Agent agent;
    private Gson gson = new Gson();

    public KineticSnmpAgent(KineticSnmpAgentConfig config) throws IOException {
        this.config = config;
        String host = config.getHost();
        int port = config.getPort();
        String address = host + "/" + port;
        agent = new Agent(address);
        agent.start();
        System.out.println("started Kinetic SNMP agent, port=" + port
                + ", host=" + host);

        List<KineticDevice> kineticDevices = config.loadKineticDevices(config
                .getMibKineticPath());

        setManagedObject(kineticDevices);
    }

    private void setManagedObject(List<KineticDevice> kineticDevices) {
        agent.unregisterManagedObject(agent.getSnmpv2MIB());
        agent.registerManagedObject(MOScalarFactory.createReadOnly(new OID(
                config.getSystemDescriptionOid()), config
                .getSystemDescription()));

        MOTableBuilder builder = new MOTableBuilder(new OID(
                config.getAgentInterfaceTableOid()));

        builder.addColumnType(SMIConstants.SYNTAX_OCTET_STRING,
                MOAccessImpl.ACCESS_READ_ONLY);

        for (KineticDevice device : kineticDevices) {
            builder.addRowValue(new OctetString(gson.toJson(device)));
        }

        agent.registerManagedObject(builder.build());
    }

    public static void main(String args[]) throws IOException {
        int maxNum = 5;
        int portBase = 2001;

        KineticSnmpAgent agent[] = new KineticSnmpAgent[maxNum];
        for (int i = 0; i < maxNum; i++){
            
            KineticSnmpAgentConfig config = new KineticSnmpAgentConfig();
            config.setPort(portBase + i);
            config.setMibKineticPath("conf/mibs/kinetic.mib" + (i+1) + ".json");
            
            agent[i] = new KineticSnmpAgent(config);
        }
        
     while (true) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
        
    }
}
