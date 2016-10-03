package com.seagate.kinetic.snmp.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class SimpleSnmpClient {

    private String address;

    private Snmp snmp;

    public SimpleSnmpClient(String address) {
        super();
        this.address = address;
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleSnmpClient(String address, String user, String password) {
        super();
        this.address = address;
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addAuth(user, password);
    }

    // Since snmp4j relies on asynch req/resp we need a listener
    // for responses which should be closed
    public void stop() throws IOException {
        snmp.close();
    }

    @SuppressWarnings("unchecked")
    private void start() throws IOException {
        @SuppressWarnings("rawtypes")
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        // Do not forget this line!
        transport.listen();
    }

    public void addAuth(String user, String password) {
        snmp.getUSM().addUser(
                new OctetString(user),
                new UsmUser(new OctetString(user), AuthMD5.ID, new OctetString(
                        password), null, null));
    }

    public String getAsString(OID oid) throws IOException {
        ResponseEvent event = get(new OID[] { oid });
        return event.getResponse().get(0).getVariable().toString();
    }

    public void getAsString(OID oids, ResponseListener listener) {
        try {
            snmp.send(getPDU(new OID[] { oids }), getTarget(), null, listener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PDU getPDU(OID oids[]) {
        PDU pdu = new ScopedPDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }

        pdu.setType(PDU.GET);
        return pdu;
    }

    public ResponseEvent get(OID oids[]) throws IOException {
        ResponseEvent event = snmp.send(getPDU(oids), getTarget(), null);
        if (event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    private Target getTarget() {
        Address targetAddress = GenericAddress.parse(address);
        UserTarget target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(5000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        target.setSecurityName(new OctetString("snmp"));
        return target;
    }

    /**
     * Normally this would return domain objects or something else than this...
     */
    public List<List<String>> getTableAsStrings(OID[] oids) {
        TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());

        List<TableEvent> events = tUtils
                .getTable(getTarget(), oids, null, null);

        List<List<String>> list = new ArrayList<List<String>>();
        for (TableEvent event : events) {
            if (event.isError()) {
                throw new RuntimeException(event.getErrorMessage());
            }
            List<String> strList = new ArrayList<String>();
            list.add(strList);
            for (VariableBinding vb : event.getColumns()) {
                strList.add(vb.getVariable().toString());
            }
        }
        return list;
    }

    public static String extractSingleString(ResponseEvent event) {
        return event.getResponse().get(0).getVariable().toString();
    }
}