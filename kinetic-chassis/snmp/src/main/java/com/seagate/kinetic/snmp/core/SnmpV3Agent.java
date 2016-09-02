package com.seagate.kinetic.snmp.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB.SnmpCommunityEntryRow;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogLevel;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;

public class SnmpV3Agent extends BaseAgent {
    private final static boolean DEBUG_FLAG = Boolean.parseBoolean(System
            .getProperty("DEBUG", "false"));

    // not needed but very useful of course
    static {
        if (DEBUG_FLAG) {
            LogFactory.setLogFactory(new Log4jLogFactory());
            BasicConfigurator.configure();
            LogFactory.getLogFactory().getRootLogger()
                    .setLogLevel(LogLevel.ALL);
        }
    }

    private String address;

    public SnmpV3Agent(String address) throws IOException {
        super(new File("bootCounter.agent"), new File(
                "/Users/Emma/agentConfig.properties"), new CommandProcessor(
                new OctetString("mytestagent".getBytes())));
        this.address = address;
    }

    /**
     * We let clients of this agent register the MO they need so this method
     * does nothing
     */
    @Override
    protected void registerManagedObjects() {
    }

    /**
     * Clients can register the MO they need
     */
    public void registerManagedObject(ManagedObject mo) {
        try {
            server.register(mo, null);
        } catch (DuplicateRegistrationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void unregisterManagedObject(MOGroup moGroup) {
        moGroup.unregisterMOs(server, getContext(moGroup));
    }

    /*
     * Empty implementation
     */
    @Override
    protected void addNotificationTargets(SnmpTargetMIB targetMIB,
            SnmpNotificationMIB notificationMIB) {
    }

    /**
     * Minimal View based Access Control
     * 
     * http://www.faqs.org/rfcs/rfc2575.html
     */
    @Override
    protected void addViews(VacmMIB vacm) {
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString(
                "snmp"), new OctetString("v3group"), StorageType.nonVolatile);

        vacmMIB.addAccess(new OctetString("v3group"), new OctetString(),
                SecurityModel.SECURITY_MODEL_USM, SecurityLevel.AUTH_NOPRIV,
                MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
                new OctetString("fullWriteView"), new OctetString(
                        "fullNotifyView"), StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("fullReadView"), new OID(
                "1.3"), new OctetString(), VacmMIB.vacmViewIncluded,
                StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("fullWriteView"), new OID(
                "1.3"), new OctetString(), VacmMIB.vacmViewIncluded,
                StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("fullNotifyView"), new OID(
                "1.3"), new OctetString(), VacmMIB.vacmViewIncluded,
                StorageType.nonVolatile);
    }

    /**
     * User based Security Model, only applicable to SNMP v.3
     * 
     */
    protected void addUsmUser(USM usm) {
        UsmUser user = new UsmUser(new OctetString("snmp"), // 账户名
                AuthMD5.ID, // encrypt protocol
                new OctetString("password"), // password
                null, // private protocol
                null // private password
        );
        usm.addUser(user.getSecurityName(), null, user);
    }

    @SuppressWarnings("unchecked")
    protected void initTransportMappings() throws IOException {
        transportMappings = new TransportMapping[1];
        Address addr = GenericAddress.parse(address);
        transportMappings[0] = TransportMappings.getInstance()
                .createTransportMapping(addr);
    }

    /**
     * Start method invokes some initialization methods needed to start the
     * agent
     * 
     * @throws IOException
     */
    public void start() throws IOException {

        init();
        addShutdownHook();
        getServer().addContext(new OctetString("public"));
        finishInit();
        run();
        sendColdStartNotification();
    }

    protected void unregisterManagedObjects() {
        // here we should unregister those objects previously registered...
    }

    /**
     * The table of community strings configured in the SNMP engine's Local
     * Configuration Datastore (LCD).
     * 
     * We only configure one, "public".
     */
    protected void addCommunities(SnmpCommunityMIB communityMIB) {
        Variable[] com2sec = new Variable[] { new OctetString("public"),
                new OctetString("cpublic"), // security name
                getAgent().getContextEngineID(), // local engine ID
                new OctetString("public"), // default context name
                new OctetString(), // transport tag
                new Integer32(StorageType.nonVolatile), // storage type
                new Integer32(RowStatus.active) // row status
        };
        SnmpCommunityEntryRow row = communityMIB.getSnmpCommunityEntry()
                .createRow(new OctetString("public2public").toSubIndex(true),
                        com2sec);
        communityMIB.getSnmpCommunityEntry().addRow(row);
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        SnmpV3Agent agent = new SnmpV3Agent("0.0.0.0/2001");
        agent.start();
        while (true) {
            Thread.sleep(5000);
        }
    }
}