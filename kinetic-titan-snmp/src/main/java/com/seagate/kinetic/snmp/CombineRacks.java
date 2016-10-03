package com.seagate.kinetic.snmp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.google.gson.Gson;

public class CombineRacks {
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
        sb.append("Usage: generateHwviewFromRackJson \n");
        sb.append("generateHwviewFromRackJson -h|-help\n");
        sb.append("generateHwviewFromRackJson -files <rackJsonFileA, rackJsonFileB, ...> \n");
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

        String racksFilesParam = CombineRacks.getArgValue("-files", args);
        String[] files = racksFilesParam.split(",");
        if (files == null || files.length == 0) {
            throw new Exception("Parameter for -files is null or empty!");
        }

        String[] rackFiles = new String[files.length];
        int j = 0;
        for (int i = 0; i < files.length; i++) {
            if (!new File(files[i]).exists()) {
                System.out.println("File: " + files[i] + " doesn't exist!");
            } else {
                rackFiles[j] = files[i];
                j++;
            }
        }

        String out = CombineRacks.getArgValue("-out", args);
        out = out == null ? DEFAULT_OUT : out;

        List<SnmpHwViewRack> snmpHwViewRackOfList = SnmpHwView
                .combineRacksToHWView(rackFiles);

        File file = new File(out);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write("{\"racks\":".getBytes());
        fileOutputStream.write(gson.toJson(snmpHwViewRackOfList).getBytes());
        fileOutputStream.write("}".getBytes());
        System.out.println(gson.toJson(snmpHwViewRackOfList));
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
