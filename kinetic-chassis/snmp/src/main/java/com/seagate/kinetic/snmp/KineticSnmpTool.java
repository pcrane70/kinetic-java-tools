package com.seagate.kinetic.snmp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.gson.Gson;

public class KineticSnmpTool {
    private static final String DEFAULT_AGENT_IP = "127.0.0.1";
    private static final String DEFAULT_AGENT_PORT = "2001";
    private static final String DEFAULT_SNMP_USER = "snmp";
    private static final String DEFAULT_SNMP_PASSWORD = "password";
    private static final String DEFAULT_IP_A_OID = "1.3.6.1.4.1.3581.12.7.2.1.1.10";
    private static final String DEFAULT_IP_B_OID = "1.3.6.1.4.1.3581.12.7.2.1.1.11";
    private static final String DEFAULT_DRIVE_OID_INDEX = "12";
    private static final String DEFAULT_DRIVE_COUNT = "84";
    private static final String DEFAULT_CHASSIS_ID = "1";
    private static final String DEFAULT_OUT = "./hwview_chassis";
    private static final String AGENT_IP = "agent.ip";
    private static final String AGENT_PORT = "agent.port";
    private static final String SNMP_USER = "user";
    private static final String SNMP_PASSWORD = "password";
    private static final String DRIVE_IPA_OID = "drive.ipa.oid";
    private static final String DRIVE_IPB_OID = "drive.ipb.oid";
    private static final String OID_START_INDEX = "oid.start.index";
    private static final String DRIVE_COUNT_ONE_CHASSIS = "drive.count";
    private static final String CHASSIS_ID = "chassis.id";
    private static final String OUTPUT_FILE_PATH = "out.file";
    private static final int OK = 0;
    private static Properties properties;

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
        sb.append("kSnmpTool -file <configrationFileAbsolutePath>\n");
        System.out.println(sb.toString());
    }

    public static void loadParametersFromConfigFile(File file)
            throws FileNotFoundException, IOException {
        properties = new Properties();
        properties.load(new FileInputStream(file));
    }

    public static String getProperty(String propertyName, String defaultValue) {
        return properties.getProperty(propertyName, defaultValue);
    }

    public static void main(String args[]) throws IOException {
        Gson gson = new Gson();

        if (hasArg("-h", args) || hasArg("-help", args)) {
            printHelp();
            System.exit(OK);
        }

        if (!hasArg("-file", args)) {
            printHelp();
            System.exit(OK);
        } else {
            String fileName = KineticSnmpTool.getArgValue("-file", args);
            File file = new File(fileName);

            loadParametersFromConfigFile(file);

            String agentIp = getProperty(AGENT_IP, DEFAULT_AGENT_IP);

            String agentPort = getProperty(AGENT_PORT, DEFAULT_AGENT_PORT);

            String user = getProperty(SNMP_USER, DEFAULT_SNMP_USER);

            String password = getProperty(SNMP_PASSWORD, DEFAULT_SNMP_PASSWORD);

            String ipAOid = getProperty(DRIVE_IPA_OID, DEFAULT_IP_A_OID);

            String ipBOid = getProperty(DRIVE_IPB_OID, DEFAULT_IP_B_OID);

            String indexS = getProperty(OID_START_INDEX,
                    DEFAULT_DRIVE_OID_INDEX);
            int index = Integer.parseInt(indexS);

            String countS = getProperty(DRIVE_COUNT_ONE_CHASSIS,
                    DEFAULT_DRIVE_COUNT);
            int count = Integer.parseInt(countS);

            String chassisId = getProperty(CHASSIS_ID, DEFAULT_CHASSIS_ID);

            String out = getProperty(OUTPUT_FILE_PATH, DEFAULT_OUT) + "_"
                    + chassisId + ".json";

            SnmpHwViewChassis snmpHwViewChassis = SnmpHwView
                    .loadFromSnmpAgents(agentIp, agentPort, user, password,
                            ipAOid, ipBOid, chassisId, index, count);

            File fileOutput = new File(out);
            if (!fileOutput.exists()) {
                fileOutput.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
            fileOutputStream.write(gson.toJson(snmpHwViewChassis).getBytes());
            System.out.println(gson.toJson(snmpHwViewChassis));
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }
}
