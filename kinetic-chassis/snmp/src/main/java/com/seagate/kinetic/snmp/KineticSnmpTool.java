package com.seagate.kinetic.snmp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

public class KineticSnmpTool {
    private static final String DEFAULT_AGENT_IPS = "127.0.0.1";
    private static final String DEFAULT_SYS_DESC_OID = ".1.3.6.1.2.1.1.1.0";
    private static final String DEFAULT_INTERFACE_TABLE_OID = ".1.3.6.1.2.1.2.2.1";
    private static final String DEFAULT_RACK_COORDINATE = "a,b,c";
    private static final String DEFAULT_OUT = "./hwview.json";
    private static final int OK = 0;

    public static String getArgValue(String argName, String args[]) {
        if (null == argName || argName.isEmpty() || args.length < 1) {
            return null;
        }

        int index = -1;
        for (int i = 0; i < args.length; i++) {
            if (argName.equalsIgnoreCase(args[i])) {
                index = i;
                break;
            }
        }

        if (index != -1 && args.length > (index + 1)) {
            if (args[index + 1].startsWith("-")) {
                throw new IllegalArgumentException("value can't start with -");
            }
            if (null == args[index + 1]) {
                throw new IllegalArgumentException("value can't be null");
            }
            return args[index + 1].trim();
        }

        return null;
    }

    public static boolean hasArg(String argName, String args[]) {
        if (null == argName || argName.isEmpty() || args.length < 1) {
            return false;
        }

        for (String arg : args) {
            if (arg.equals(argName)) {
                return true;
            }
        }

        return false;
    }

    public static void printHelp() {
        StringBuffer sb = new StringBuffer();
        sb.append("Usage: kSnmpTool \n");
        sb.append("kSnmpTool -h|-help\n");
        sb.append("kSnmpTool -agentIps <agentIp:agentport,agentIp:agentport,...> -interfaceTableOid <interfaceTableOid> -rackCoordinate <rackCoordinate> -out <hvOutputFile>\n");
        System.out.println(sb.toString());
    }

    public static void main(String args[]) throws IOException {
        Gson gson = new Gson();
        if (hasArg("-h", args) || hasArg("-help", args)) {
            printHelp();
            System.exit(OK);
        }

        String agentIps = KineticSnmpTool.getArgValue("-agentIps", args);
        agentIps = agentIps == null ? DEFAULT_AGENT_IPS : agentIps;

        String SysDescOid = DEFAULT_SYS_DESC_OID;

        String interfaceTableOid = KineticSnmpTool.getArgValue(
                "-interfaceTableOid", args);
        interfaceTableOid = interfaceTableOid == null ? DEFAULT_INTERFACE_TABLE_OID
                : interfaceTableOid;

        String rackCoordinate = KineticSnmpTool.getArgValue("-rackCoordinate",
                args);
        rackCoordinate = rackCoordinate == null ? DEFAULT_RACK_COORDINATE
                : rackCoordinate;

        String out = KineticSnmpTool.getArgValue("-out", args);
        out = out == null ? DEFAULT_OUT : out;

        SnmpHwView snmpHwView = SnmpHwView.loadFromSnmpAgents(agentIps,
                SysDescOid, interfaceTableOid, rackCoordinate);

        File file = new File(out);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(gson.toJson(snmpHwView).getBytes());
        System.out.println(gson.toJson(snmpHwView));
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
