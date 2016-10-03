package com.seagate.kinetic.snmp;

import java.io.File;
import java.io.FileOutputStream;

import com.google.gson.Gson;

public class CombineChassis {
    private static final String DEFAULT_RACK_ID = "1";
    private static final String DEFAULT_RACK_COORDINATE = "a,b,c";
    private static final String DEFAULT_OUT = "./hwview_rack";
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
        sb.append("Usage: generateRackJsonFromChassisJson \n");
        sb.append("generateRackJsonFromChassisJson -h|-help\n");
        sb.append("generateRackJsonFromChassisJson -files <chassisJsonFileA, chassisJsonFileB, ...> [-rackId <rackId> -rackCoordinate <rackCoordinate> -out <hvOutputFile>] \n");
        System.out.println(sb.toString());
    }

    public static void main(String args[]) throws Exception {
        Gson gson = new Gson();

        if (hasArg("-h", args) || hasArg("-help", args)) {
            printHelp();
            System.exit(OK);
        }

        if (!hasArg("-files", args)) {
            printHelp();
            System.exit(OK);
        }

        String chassisFilesParam = CombineChassis.getArgValue("-files", args);
        String[] files = chassisFilesParam.split(",");
        if (files == null || files.length == 0) {
            throw new Exception("Parameter for -files is null or empty!");
        }

        String[] chassisFiles = new String[files.length];
        int j = 0;
        for (int i = 0; i < files.length; i++) {
            if (!new File(files[i]).exists()) {
                System.out.println("File: " + files[i] + " doesn't exist!");
            } else {
                chassisFiles[j] = files[i];
                j++;
            }
        }

        String rackId = CombineChassis.getArgValue("-rackId", args);
        rackId = rackId == null ? DEFAULT_RACK_ID : rackId;

        String rackCoordinate = CombineChassis.getArgValue("-rackCoordinate",
                args);
        rackCoordinate = rackCoordinate == null ? DEFAULT_RACK_COORDINATE
                : rackCoordinate;

        String out = CombineChassis.getArgValue("-out", args);
        out = out == null ? DEFAULT_OUT : out;
        out = out + "_" + rackId + ".json";

        SnmpHwViewRack snmpHwViewRack = SnmpHwView.combineChassisToRack(rackId,
                rackCoordinate, chassisFiles);

        File file = new File(out);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(gson.toJson(snmpHwViewRack).getBytes());
        System.out.println(gson.toJson(snmpHwViewRack));
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
