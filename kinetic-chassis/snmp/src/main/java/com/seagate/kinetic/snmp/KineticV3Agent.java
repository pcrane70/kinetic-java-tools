package com.seagate.kinetic.snmp;

import java.io.IOException;

import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.DefaultMOMutableTableModel;
import org.snmp4j.agent.mo.DefaultMOTable;
import org.snmp4j.agent.mo.DefaultMOTableRow;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOMutableColumn;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableSubIndex;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.Variable;

import com.seagate.kinetic.snmp.conf.KineticSnmpAgentConfig;
import com.seagate.kinetic.snmp.core.SnmpV3Agent;

public class KineticV3Agent {
    private SnmpV3Agent agent;
    private static final String kineticIpTableEntryOid = KineticSnmpAgentConfig
            .getIpOidRoot();
    private static final String IP_NOT_SET = "0.0.0.0";

    public KineticV3Agent(String address) throws IOException {
        agent = new SnmpV3Agent(address);
        setManagedObjects();
        agent.start();

        System.out.println("SNMP agent has been started at " + address);
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
    private void setManagedObjects() {
        final Object[][] columnKineticIpDefined = {
                { 1, "1", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 2, "2", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 3, "3", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 4, "4", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 5, "5", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 6, "6", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 7, "7", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 8, "8", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 9, "9", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 10, "10", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING },
                { 11, "11", MOAccessImpl.ACCESS_READ_ONLY,
                        SMIConstants.SYNTAX_OCTET_STRING } };

        int columnLength = columnKineticIpDefined.length;
        MOColumn[] kineticIpColumns = new MOColumn[columnLength];
        for (int i = 0; i < columnLength; i++) {
            Object[] acol = columnKineticIpDefined[i];
            int index = (Integer) acol[0];
            String name = (String) acol[1];
            MOAccess access = (MOAccess) acol[2];
            int operType = (Integer) acol[3];

            Variable valueDefault = new Integer32(1);
            kineticIpColumns[i] = new MOMutableColumn<Variable>(index,
                    operType, access, valueDefault, true);
        }

        DefaultMOTable kineticIpTable = new DefaultMOTable(new OID(
                kineticIpTableEntryOid), new MOTableIndex(
                new MOTableSubIndex[] { new MOTableSubIndex(new OID(
                        kineticIpTableEntryOid + ".1001"),
                        SMIConstants.SYNTAX_OCTET_STRING, 1, 16) }, true),
                kineticIpColumns,
                new DefaultMOMutableTableModel<DefaultMOTableRow>());

        for (int rowId = 1; rowId < 100; rowId++) {
            Variable[] values = new Variable[12];

            // set IP outside，if 1.3.6.1.4.1.3581.12.7.2.1.1.[1~9] begin，no
            // setting，showing valuetofill...，could be changed.
            for (int i = 0; i < 9; i++) {
                values[i] = new OctetString("valuetofill: " + (i + 1) + "."
                        + rowId);
            }

            // set value in 1.3.6.1.4.1.3581.12.7.2.1.1.[10~11].[12~95]，if no in
            // this range，its "0.0.0.0".
            if (rowId < 12 || rowId > 95) {
                values[9] = new OctetString(IP_NOT_SET);
                values[10] = new OctetString(IP_NOT_SET);

            } else {
                values[9] = new OctetString(
                        KineticSnmpAgentConfig.getIpOfPlaneA(rowId));
                values[10] = new OctetString(
                        KineticSnmpAgentConfig.getIpOfPlaneB(rowId));
            }

            kineticIpTable.addRow(new DefaultMOTableRow(new OID("" + rowId),
                    values));
        }

        agent.registerManagedObject(kineticIpTable);
    }

    public static void main(String args[]) throws IOException {
        new KineticV3Agent(KineticSnmpAgentConfig.getAgentAddress());
    }
}
