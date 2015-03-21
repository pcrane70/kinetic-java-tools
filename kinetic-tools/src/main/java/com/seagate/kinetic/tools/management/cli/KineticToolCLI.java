/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.cli.impl.DeviceDiscovery;
import com.seagate.kinetic.tools.management.cli.impl.FirmwareDownloader;
import com.seagate.kinetic.tools.management.cli.impl.FirmwareVersionChecker;
import com.seagate.kinetic.tools.management.cli.impl.InstantErase;
import com.seagate.kinetic.tools.management.cli.impl.SetClusterVersion;
import com.seagate.kinetic.tools.management.cli.impl.SetErasePin;
import com.seagate.kinetic.tools.management.cli.impl.SetSecurity;
import com.seagate.kinetic.tools.management.cli.impl.SmokeTestRunner;

/**
 *
 * KineticClient command line tool, support setup security getlog and help
 * <p>
 *
 *
 */
public class KineticToolCLI {
    private static final int OK = 0;
    private static final String DEFAULT_DISCOVER_TIME = "30";
    private static final String DEFAULT_DRIVE_OUTPUT_FILE = "drives";
    private final Map<String, List<String>> legalArguments = new HashMap<String, List<String>>();

    public KineticToolCLI() throws KineticException {
        String rootArg = "-help";
        List<String> subArgs = new ArrayList<String>();
        legalArguments.put(rootArg, subArgs);

        rootArg = "-h";
        subArgs = new ArrayList<String>();
        legalArguments.put(rootArg, subArgs);

        rootArg = "-discover";
        subArgs = new ArrayList<String>();
        subArgs.add("-timeout");
        subArgs.add("-out");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-firmwaredownload";
        subArgs = new ArrayList<String>();
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-setsecurity";
        subArgs = new ArrayList<String>();
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-seterasepin";
        subArgs = new ArrayList<String>();
        subArgs.add("-oldpin");
        subArgs.add("-newpin");
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-setclusterversion";
        subArgs = new ArrayList<String>();
        subArgs.add("-clversion");
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-instanterase";
        subArgs = new ArrayList<String>();
        subArgs.add("-pin");
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-checkversion";
        subArgs = new ArrayList<String>();
        subArgs.add("-v");
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-runsmoketest";
        subArgs = new ArrayList<String>();
        subArgs.add("-in");
        legalArguments.put(rootArg, subArgs);
    }

    public static void printHelp() {
        StringBuffer sb = new StringBuffer();
        sb.append("Usage: ktool <-discover|-firmwaredownload|-checkversion|-setclusterversion|-setsecurity|-seterasepin|-instanterase|-runsmoketest>\n");
        sb.append("ktool -h|-help\n");
        sb.append("ktool -discover [-out <driveListOutputFile>] [-timeout <timeoutInSecond>]\n");
        sb.append("ktool -firmwaredownload <fmFile> <-in <driveListInputFile>>\n");
        sb.append("ktool -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>>\n");
        sb.append("ktool -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>>\n");
        sb.append("ktool -instanterase <-pin <erasePinInString>> <-in <driveListInputFile>>\n");
        sb.append("ktool -setclusterversion <-clversion <newClusterVersionInString>> <-in <driveListInputFile>>\n");
        sb.append("ktool -setsecurity <securityFile> <-in <driveListInputFile>>\n");
        sb.append("ktool -runsmoketest <-in <driveListInputFile>>\n");
        System.out.println(sb.toString());
    }

    public String getArgValue(String argName, String args[]) {
        if (null == argName || argName.isEmpty() || args.length <= 1) {
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

    public void printSuccessResult() {
        System.out.println("SUCCESS");
    }

    public void validateArgNames(String args[]) throws Exception {
        if (args == null || args.length <= 0) {
            return;
        }

        String rootArg = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (legalArguments.get(arg.toLowerCase()) != null) {
                    rootArg = arg;
                    break;
                }
            }
        }

        if (rootArg == null || !validateArgNames(rootArg, args)) {
            throw new Exception(
                    "No supported command in the request, please see usage...\n");
        }
    }

    @SuppressWarnings("static-access")
    public static void main(String args[]) {
        if (args.length < 1) {
            printHelp();
            System.exit(OK);
        }

        KineticToolCLI kineticClusterMgmtCLI = null;
        try {
            kineticClusterMgmtCLI = new KineticToolCLI();
            kineticClusterMgmtCLI.validateArgNames(args);
            if (args[0].equalsIgnoreCase("-help")
                    || args[0].equalsIgnoreCase("-h")) {
                kineticClusterMgmtCLI.printHelp();
                System.exit(OK);
            } else if (args[0].equalsIgnoreCase("-discover")) {
                String timeoutInString = kineticClusterMgmtCLI.getArgValue(
                        "-timeout", args);
                timeoutInString = timeoutInString == null ? DEFAULT_DISCOVER_TIME
                        : timeoutInString;

                int timeout = Integer.parseInt(timeoutInString);

                String driveListOutputFile = kineticClusterMgmtCLI.getArgValue(
                        "-out", args);

                long time = System.currentTimeMillis();

                String driveDefaultName = DEFAULT_DRIVE_OUTPUT_FILE + "_"
                        + String.valueOf(time);

                driveListOutputFile = driveListOutputFile == null ? driveDefaultName
                        : driveListOutputFile;

                DeviceDiscovery deviceDiscovery = new DeviceDiscovery();
                System.out.println("Discovering devices..., please wait "
                        + timeout + "s" + "\n");
                TimeUnit.SECONDS.sleep(timeout);
                System.out.println(DeviceDiscovery.persistToFile(
                        deviceDiscovery.listDevices(), driveListOutputFile));

                System.out.println("Discovered "
                        + deviceDiscovery.listDevices().size() + " drives");
            } else if (args[0].equalsIgnoreCase("-firmwaredownload")) {
                String firmwareFile = kineticClusterMgmtCLI.getArgValue(
                        "-firmwaredownload", args);
                String nodesLogFile = kineticClusterMgmtCLI.getArgValue("-in",
                        args);

                FirmwareDownloader downloader = new FirmwareDownloader(
                        firmwareFile, nodesLogFile);
                downloader.updateFirmware();
            } else if (args[0].equalsIgnoreCase("-setsecurity")) {
                String securityFile = kineticClusterMgmtCLI.getArgValue(
                        "-setsecurity", args);
                String driveListInputFile = kineticClusterMgmtCLI.getArgValue(
                        "-in", args);

                SetSecurity setSecurityer = new SetSecurity(securityFile,
                        driveListInputFile);
                setSecurityer.setSecurity();
            } else if (args[0].equalsIgnoreCase("-seterasepin")) {
                String oldErasePin = kineticClusterMgmtCLI.getArgValue(
                        "-oldpin", args);
                String newErasePin = kineticClusterMgmtCLI.getArgValue(
                        "-newpin", args);
                String driveListInputFile = kineticClusterMgmtCLI.getArgValue(
                        "-in", args);

                SetErasePin setErasePiner = new SetErasePin(oldErasePin,
                        newErasePin, driveListInputFile);
                setErasePiner.setErasePin();
            } else if (args[0].equalsIgnoreCase("-setclusterversion")) {
                String newClusterVersion = kineticClusterMgmtCLI.getArgValue(
                        "-clversion", args);
                String driveListInputFile = kineticClusterMgmtCLI.getArgValue(
                        "-in", args);

                SetClusterVersion SetClusterVersioner = new SetClusterVersion(
                        newClusterVersion, driveListInputFile);
                SetClusterVersioner.setClusterVersion();
            } else if (args[0].equalsIgnoreCase("-instanterase")) {
                String erasePin = kineticClusterMgmtCLI.getArgValue("-pin",
                        args);
                String driveListInputFile = kineticClusterMgmtCLI.getArgValue(
                        "-in", args);

                InstantErase InstantEraser = new InstantErase(erasePin,
                        driveListInputFile);
                InstantEraser.instantErase();
            } else if (args[0].equalsIgnoreCase("-checkversion")) {
                String expectVersion = kineticClusterMgmtCLI.getArgValue("-v",
                        args);
                String driveListInputFile = kineticClusterMgmtCLI.getArgValue(
                        "-in", args);

                FirmwareVersionChecker checker = new FirmwareVersionChecker(
                        expectVersion, driveListInputFile);
                checker.checkFirmwareVersion();
            } else if (args[0].equalsIgnoreCase("-runsmoketest")) {
                String driveListInputFile = kineticClusterMgmtCLI.getArgValue(
                        "-in", args);

                new SmokeTestRunner(driveListInputFile).runSmokeTests();
            } else {
                printHelp();
            }
        } catch (KineticException ke) {
            if (ke.getResponseMessage() != null
                    && ke.getResponseMessage().getCommand() != null
                    && ke.getResponseMessage().getCommand().getStatus() != null
                    && ke.getResponseMessage().getCommand().getStatus()
                            .getCode() != null) {
                System.out.println(ke.getResponseMessage().getCommand()
                        .getStatus().getCode());
            } else {
                ke.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printHelp();
        }

        System.exit(OK);
    }

    private boolean contain(List<String> list, String item) {
        for (String str : list) {
            if (str.equalsIgnoreCase(item)) {
                return true;
            }
        }

        return false;
    }

    private boolean validateArgNames(String rootArg, String args[]) {
        List<String> subArgs = legalArguments.get(rootArg);
        for (String arg : args) {
            if (arg.equals(rootArg)) {
                continue;
            }

            if (arg.startsWith("-") && !contain(subArgs, arg)) {
                return false;
            }
        }

        return true;
    }

}
