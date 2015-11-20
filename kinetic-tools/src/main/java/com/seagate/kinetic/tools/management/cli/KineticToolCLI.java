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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.cli.impl.CheckFirmwareVersion;
import com.seagate.kinetic.tools.management.cli.impl.DefaultCommandInvoker;
import com.seagate.kinetic.tools.management.cli.impl.DeviceDiscovery;
import com.seagate.kinetic.tools.management.cli.impl.FirmwareDownload;
import com.seagate.kinetic.tools.management.cli.impl.GetLog;
import com.seagate.kinetic.tools.management.cli.impl.GetVendorSpecificDeviceLog;
import com.seagate.kinetic.tools.management.cli.impl.InstantErase;
import com.seagate.kinetic.tools.management.cli.impl.Invoker;
import com.seagate.kinetic.tools.management.cli.impl.LockDevice;
import com.seagate.kinetic.tools.management.cli.impl.PerfRunner;
import com.seagate.kinetic.tools.management.cli.impl.PingReachableDrive;
import com.seagate.kinetic.tools.management.cli.impl.SecureErase;
import com.seagate.kinetic.tools.management.cli.impl.SetClusterVersion;
import com.seagate.kinetic.tools.management.cli.impl.SetErasePin;
import com.seagate.kinetic.tools.management.cli.impl.SetLockPin;
import com.seagate.kinetic.tools.management.cli.impl.SetSecurity;
import com.seagate.kinetic.tools.management.cli.impl.SmokeTestRunner;
import com.seagate.kinetic.tools.management.cli.impl.UnLockDevice;
import com.seagate.kinetic.tools.management.common.util.JsonConvertUtil;

/**
 *
 * KineticClient command line tool, support setup security getlog and help
 * <p>
 *
 *
 */
public class KineticToolCLI {
    private static final String DEFAULT_PERF_THREADS = "10";
    private static final String DEFAULT_DISTRIBUTION = "uniform";
    private static final String DEFAULT_READ_PROPORTION = "0";
    private static final String DEFAULT_INSERT_PROPORTION = "1";
    private static final String DEFAULT_CONNECTION_PER_DRIVE = "1";
    private static final String DEFAULT_PERF_OPERATION_COUNT = "1000";
    private static final String DEFAULT_PERF_RECORD_COUNT = "10000";
    private static final String DEFAULT_PERF_VALUE_SIZE = "1048576";
    private static final int OK = 0;
    private static final String DEFAULT_USE_SSL = "true";
    private static final String DEFAULT_NON_USE_SSL = "false";
    private static final String DEFAULT_CLUSTER_VERSION = "0";
    private static final String DEFAULT_IDENTITY = "1";
    private static final String DEFAULT_KEY = "asdfasdf";
    private static final String DEFAULT_REQUEST_TIMEOUT_IN_SECOND = "60";
    private static final String DEFAULT_ISE_REQUEST_TIMEOUT_IN_SECOND = "180";
    private static final String DEFAULT_DISCOVER_TIME_IN_SECOND = "30";
    private static final String DEFAULT_DRIVE_OUTPUT_FILE = "drives";
    private static final String DEFAULT_GET_LOG_TYPE = "all";
    private static final String DEFAULT_GET_LOG_OUTPUT_FILE = "getlogs";
    private static final String DEFAULT_PING_SUCCESS_DRIVE_OUTPUT_FILE = "pingsuccessdrives";
    private static final String DEFAULT_GET_VENDOR_SPECIFIC_LOG_OUTPUT_FILE = "vendorspecificlogs";
    private static final String SUBNET_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final Logger logger = Logger.getLogger(KineticToolCLI.class
            .getName());

    private final Map<String, List<String>> legalArguments = new HashMap<String, List<String>>();

    public KineticToolCLI() throws KineticException {
        String rootArg = "-help";
        List<String> subArgs = new ArrayList<String>();
        legalArguments.put(rootArg, subArgs);

        rootArg = "-h";
        subArgs = new ArrayList<String>();
        legalArguments.put(rootArg, subArgs);

        rootArg = "-discover";
        subArgs = initSubArgs();
        subArgs.add("-timeout");
        subArgs.add("-out");
        subArgs.add("-subnet");
        subArgs.add("-scoped");
        subArgs.add("-startip");
        subArgs.add("-endip");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-ping";
        subArgs = initSubArgs();
        subArgs.add("-in");
        subArgs.add("-out");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-firmwaredownload";
        subArgs = initSubArgs();
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-setsecurity";
        subArgs = initSubArgs();
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-seterasepin";
        subArgs = initSubArgs();
        subArgs.add("-oldpin");
        subArgs.add("-newpin");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-setlockpin";
        subArgs = initSubArgs();
        subArgs.add("-oldpin");
        subArgs.add("-newpin");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-setclusterversion";
        subArgs = initSubArgs();
        subArgs.add("-newclversion");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-instanterase";
        subArgs = initSubArgs();
        subArgs.add("-pin");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-secureerase";
        subArgs = initSubArgs();
        subArgs.add("-pin");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-checkversion";
        subArgs = initSubArgs();
        subArgs.add("-v");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-getlog";
        subArgs = initSubArgs();
        subArgs.add("-in");
        subArgs.add("-out");
        subArgs.add("-type");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-getvendorspecificdevicelog";
        subArgs = initSubArgs();
        subArgs.add("-in");
        subArgs.add("-out");
        subArgs.add("-name");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-lockdevice";
        subArgs = initSubArgs();
        subArgs.add("-pin");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-unlockdevice";
        subArgs = initSubArgs();
        subArgs.add("-pin");
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-runsmoketest";
        subArgs = initSubArgs();
        subArgs.add("-in");
        subArgs.add("-format");
        legalArguments.put(rootArg, subArgs);

        rootArg = "-perf";
        subArgs.add("-in");
        subArgs.add("-valuesize");
        subArgs.add("-recordcount");
        subArgs.add("-operationcount");
        subArgs.add("-connectionperdrive");
        subArgs.add("-readproportion");
        subArgs.add("-insertproportion");
        subArgs.add("-distribution");
        subArgs.add("-threads");
        subArgs.add("-format");

        legalArguments.put(rootArg, subArgs);
    }

    public static void printHelp() {
        StringBuffer sb = new StringBuffer();
        sb.append("Usage: ktool <-discover|-firmwaredownload|-checkversion|-setclusterversion|-setsecurity|-seterasepin|-instanterase|-runsmoketest>\n");
        sb.append("ktool -h|-help\n");
        sb.append("ktool -discover [-out <driveListOutputFile>] [-format <chassisjson|racksjson>] [-timeout <timeoutInSecond>] [-subnet <subnet>] [-scoped] [-startip <startIp>] [-endip <endIp>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -ping <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-out <driveListOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -firmwaredownload <fmFile> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -setlockpin <-oldpin <oldLockPinInString>> <-newpin <newLockPinInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -instanterase <-pin <erasePinInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -secureerase <-pin <erasePinInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -setclusterversion <-newclversion <newClusterVersionInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -setsecurity <securityFile> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -getlog <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-out <logOutputFile>] [-type <utilization|temperature|capacity|configuration|message|statistic|limits|all>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -getvendorspecificdevicelog <-name <vendorspecificname>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-out <logOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -lockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -unlockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]\n");
        sb.append("ktool -runsmoketest <-in <driveListInputFile>> [-format <chassisjson|racksjson>]\n");
        sb.append("ktool -perf <-in <driveListInputFile>> [-format <chassisjson|racksjson>] [-valuesize <valueSizeInByte>] [-recordcount <recordCountForPrepare>] [-operationcount <realOperationCount>] [-connectionperdrive <connectionPerDrive>] [-readproportion <readProportion>] [-insertproportion <insertProportion>] [-distribution <distribution>] [-threads <threads_number>]\n");
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

    public boolean hasArg(String argName, String args[]) {
        if (null == argName || argName.isEmpty() || args.length <= 1) {
            return false;
        }

        for (String arg : args) {
            if (arg.equals(argName)) {
                return true;
            }
        }

        return false;
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

        KineticToolCLI kineticToolCLI = null;
        Invoker invoker = new DefaultCommandInvoker();

        try {
            kineticToolCLI = new KineticToolCLI();
            kineticToolCLI.validateArgNames(args);

            String useSslInString = kineticToolCLI.getArgValue("-usessl", args);
            if (args[0].equalsIgnoreCase("-firmwaredownload")
                    || args[0].equalsIgnoreCase("-ping")
                    || args[0].equalsIgnoreCase("-discover")
                    || args[0].equalsIgnoreCase("-getlog")
                    || args[0].equalsIgnoreCase("-getvendorspecificdevicelog")
                    || args[0].equalsIgnoreCase("-checkversion")) {
                useSslInString = useSslInString == null ? DEFAULT_NON_USE_SSL
                        : useSslInString;
            } else {
                useSslInString = useSslInString == null ? DEFAULT_USE_SSL
                        : useSslInString;
            }

            boolean useSsl = Boolean.parseBoolean(useSslInString);

            String clusterVersionInString = kineticToolCLI.getArgValue(
                    "-clversion", args);
            clusterVersionInString = clusterVersionInString == null ? DEFAULT_CLUSTER_VERSION
                    : clusterVersionInString;
            long clusterVersion = Long.parseLong(clusterVersionInString);

            String identityInString = kineticToolCLI.getArgValue("-identity",
                    args);
            identityInString = identityInString == null ? DEFAULT_IDENTITY
                    : identityInString;
            long identity = Long.parseLong(identityInString);

            String key = kineticToolCLI.getArgValue("-key", args);
            key = key == null ? DEFAULT_KEY : key;

            String requestTimeoutInString = kineticToolCLI.getArgValue(
                    "-reqtimeout", args);

            if (args[0].equalsIgnoreCase("-secureerase")
                    || args[0].equalsIgnoreCase("-instanterase")) {
                requestTimeoutInString = requestTimeoutInString == null ? DEFAULT_ISE_REQUEST_TIMEOUT_IN_SECOND
                        : requestTimeoutInString;
            } else {
                requestTimeoutInString = requestTimeoutInString == null ? DEFAULT_REQUEST_TIMEOUT_IN_SECOND
                        : requestTimeoutInString;
            }
            long requestTimeout = Long.parseLong(requestTimeoutInString);

            if (args[0].equalsIgnoreCase("-help")
                    || args[0].equalsIgnoreCase("-h")) {
                kineticToolCLI.printHelp();
                System.exit(OK);
            } else if (args[0].equalsIgnoreCase("-discover")) {
                String timeoutInString = kineticToolCLI.getArgValue("-timeout",
                        args);
                timeoutInString = timeoutInString == null ? DEFAULT_DISCOVER_TIME_IN_SECOND
                        : timeoutInString;

                int timeout = Integer.parseInt(timeoutInString);

                String driveListOutputFile = kineticToolCLI.getArgValue("-out",
                        args);

                long time = System.currentTimeMillis();

                String driveDefaultName = DEFAULT_DRIVE_OUTPUT_FILE + "_"
                        + String.valueOf(time);

                String formatFlag = "";
                if (kineticToolCLI.hasArg("-format", args)) {
                    formatFlag = kineticToolCLI.getArgValue("-format", args);
                    if (!formatFlag.equalsIgnoreCase("chassisjson")
                            && !formatFlag.equalsIgnoreCase("racksjson")) {
                        throw new Exception(
                                "format parameters illegal, it should be chassisjson or racksjson");
                    }
                }

                String subnet = kineticToolCLI.getArgValue("-subnet", args);

                // scope
                if (kineticToolCLI.hasArg("-scoped", args)) {
                    if (null != subnet) {
                        throw new Exception(
                                "Can't set subnet and scoped at the same time.");
                    }

                    String start = kineticToolCLI.getArgValue("-startip", args);
                    String end = kineticToolCLI.getArgValue("-endip", args);

                    driveListOutputFile = driveListOutputFile == null ? driveDefaultName
                            : driveListOutputFile;

                    DeviceDiscovery deviceDiscovery = new DeviceDiscovery(
                            start, end);
                    System.out
                            .println("Discovering devices with scoped: startIp="
                                    + start
                                    + ", endIp="
                                    + end
                                    + ", please wait " + timeout + "s" + "\n");
                    TimeUnit.SECONDS.sleep(timeout);

                    String toolHome = System.getProperty("kinetic.tools.out",
                            ".");
                    String rootDir = toolHome + File.separator + "out"
                            + File.separator;

                    driveListOutputFile = driveListOutputFile == null ? rootDir
                            + driveDefaultName : rootDir + driveListOutputFile;

                    logger.info(DeviceDiscovery.persistToFile(
                            deviceDiscovery.listDevices(), driveListOutputFile,
                            formatFlag));

                    System.out.println("Discovered "
                            + deviceDiscovery.listDevices().size()
                            + " drives, persist drives info in "
                            + driveListOutputFile);
                } else if (null != subnet) {

                    if (!kineticToolCLI.validateSubnet(subnet)) {
                        throw new Exception(
                                "Invalid subnet format, for instance: \"-subnet 192.168.10\"");
                    }

                    driveListOutputFile = driveListOutputFile == null ? driveDefaultName
                            : driveListOutputFile;

                    invoker.execute(new PingReachableDrive(subnet,
                            driveListOutputFile, useSsl, clusterVersion,
                            identity, key, requestTimeout, formatFlag));
                } else {
                    DeviceDiscovery deviceDiscovery = new DeviceDiscovery();
                    System.out.println("Discovering devices..., please wait "
                            + timeout + "s" + "\n");
                    TimeUnit.SECONDS.sleep(timeout);

                    String toolHome = System.getProperty("kinetic.tools.out",
                            ".");
                    String rootDir = toolHome + File.separator + "out"
                            + File.separator;

                    driveListOutputFile = driveListOutputFile == null ? rootDir
                            + driveDefaultName : rootDir + driveListOutputFile;

                    logger.info(DeviceDiscovery.persistToFile(
                            deviceDiscovery.listDevices(), driveListOutputFile,
                            formatFlag));

                    System.out.println("Discovered "
                            + deviceDiscovery.listDevices().size()
                            + " drives, persist drives info in "
                            + driveListOutputFile);
                }
            } else if (args[0].equalsIgnoreCase("-ping")) {
                String driveInputListFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveInputListFile = kineticToolCLI.checkInputfileFormat(args,
                        driveInputListFile);

                String driveListOutputFile = kineticToolCLI.getArgValue("-out",
                        args);

                String driveDefaultName = DEFAULT_PING_SUCCESS_DRIVE_OUTPUT_FILE
                        + "_" + String.valueOf(System.currentTimeMillis());
                driveListOutputFile = driveListOutputFile == null ? driveDefaultName
                        : driveListOutputFile;

                invoker.execute(new PingReachableDrive(driveInputListFile,
                        driveListOutputFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-firmwaredownload")) {
                String firmwareFile = kineticToolCLI.getArgValue(
                        "-firmwaredownload", args);

                String driveInputListFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveInputListFile = kineticToolCLI.checkInputfileFormat(args,
                        driveInputListFile);

                invoker.execute(new FirmwareDownload(firmwareFile,
                        driveInputListFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-setsecurity")) {
                String securityFile = kineticToolCLI.getArgValue(
                        "-setsecurity", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new SetSecurity(securityFile,
                        driveListInputFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-seterasepin")) {
                String oldErasePin = kineticToolCLI
                        .getArgValue("-oldpin", args);
                String newErasePin = kineticToolCLI
                        .getArgValue("-newpin", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new SetErasePin(oldErasePin, newErasePin,
                        driveListInputFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-setclusterversion")) {
                String newClusterVersion = kineticToolCLI.getArgValue(
                        "-newclversion", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new SetClusterVersion(newClusterVersion,
                        driveListInputFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-instanterase")) {
                String erasePin = kineticToolCLI.getArgValue("-pin", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new InstantErase(erasePin, driveListInputFile,
                        useSsl, clusterVersion, identity, key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-secureerase")) {
                String erasePin = kineticToolCLI.getArgValue("-pin", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new SecureErase(erasePin, driveListInputFile,
                        useSsl, clusterVersion, identity, key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-checkversion")) {
                String expectVersion = kineticToolCLI.getArgValue("-v", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new CheckFirmwareVersion(expectVersion,
                        driveListInputFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-setlockpin")) {
                String oldLockPin = kineticToolCLI.getArgValue("-oldpin", args);
                String newLockPin = kineticToolCLI.getArgValue("-newpin", args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new SetLockPin(oldLockPin, newLockPin,
                        driveListInputFile, useSsl, clusterVersion, identity,
                        key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-getlog")) {
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                String logOutputFile = kineticToolCLI.getArgValue("-out", args);
                logOutputFile = logOutputFile == null ? DEFAULT_GET_LOG_OUTPUT_FILE
                        + "_" + String.valueOf(System.currentTimeMillis())
                        : logOutputFile;

                String logType = kineticToolCLI.getArgValue("-type", args);
                logType = logType == null ? DEFAULT_GET_LOG_TYPE : logType;

                invoker.execute(new GetLog(driveListInputFile, logOutputFile,
                        logType, useSsl, clusterVersion, identity, key,
                        requestTimeout));
            } else if (args[0].equalsIgnoreCase("-getvendorspecificdevicelog")) {
                String vendorspecificname = kineticToolCLI.getArgValue("-name",
                        args);
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                String logOutputFile = kineticToolCLI.getArgValue("-out", args);
                logOutputFile = logOutputFile == null ? DEFAULT_GET_VENDOR_SPECIFIC_LOG_OUTPUT_FILE
                        + "_" + String.valueOf(System.currentTimeMillis())
                        : logOutputFile;

                invoker.execute(new GetVendorSpecificDeviceLog(
                        vendorspecificname, driveListInputFile, logOutputFile,
                        useSsl, clusterVersion, identity, key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-lockdevice")) {
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                String lockPin = kineticToolCLI.getArgValue("-pin", args);

                invoker.execute(new LockDevice(driveListInputFile, lockPin,
                        useSsl, clusterVersion, identity, key, requestTimeout));

            } else if (args[0].equalsIgnoreCase("-unlockdevice")) {
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                String unLockPin = kineticToolCLI.getArgValue("-pin", args);

                invoker.execute(new UnLockDevice(driveListInputFile, unLockPin,
                        useSsl, clusterVersion, identity, key, requestTimeout));
            } else if (args[0].equalsIgnoreCase("-runsmoketest")) {
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                invoker.execute(new SmokeTestRunner(driveListInputFile));
            } else if (args[0].equalsIgnoreCase("-perf")) {
                String driveListInputFile = kineticToolCLI.getArgValue("-in",
                        args);

                driveListInputFile = kineticToolCLI.checkInputfileFormat(args,
                        driveListInputFile);

                String valueSize = kineticToolCLI.getArgValue("-valuesize",
                        args);
                valueSize = valueSize == null ? DEFAULT_PERF_VALUE_SIZE
                        : valueSize;

                String recordCount = kineticToolCLI.getArgValue("-recordcount",
                        args);
                recordCount = recordCount == null ? DEFAULT_PERF_RECORD_COUNT
                        : recordCount;

                String operationCount = kineticToolCLI.getArgValue(
                        "-operationcount", args);
                operationCount = operationCount == null ? DEFAULT_PERF_OPERATION_COUNT
                        : operationCount;

                String connectionPerDrive = kineticToolCLI.getArgValue(
                        "-connectionperdrive", args);
                connectionPerDrive = connectionPerDrive == null ? DEFAULT_CONNECTION_PER_DRIVE
                        : connectionPerDrive;

                String readProportion = kineticToolCLI.getArgValue(
                        "-readproportion", args);
                readProportion = readProportion == null ? DEFAULT_READ_PROPORTION
                        : readProportion;

                String insertProportion = kineticToolCLI.getArgValue(
                        "-insertproportion", args);
                insertProportion = insertProportion == null ? DEFAULT_INSERT_PROPORTION
                        : insertProportion;

                String distribution = kineticToolCLI.getArgValue(
                        "-distribution", args);
                distribution = distribution == null ? DEFAULT_DISTRIBUTION
                        : distribution;

                String threads = kineticToolCLI.getArgValue("-threads", args);
                threads = threads == null ? DEFAULT_PERF_THREADS : threads;

                invoker.execute(new PerfRunner(driveListInputFile, valueSize,
                        recordCount, operationCount, connectionPerDrive,
                        readProportion, insertProportion, distribution, threads));
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

    private List<String> initSubArgs() {
        List<String> subArgs;
        subArgs = new ArrayList<String>();
        subArgs.add("-usessl");
        subArgs.add("-clversion");
        subArgs.add("-identity");
        subArgs.add("-key");
        subArgs.add("-reqtimeout");
        return subArgs;
    }

    private boolean validateSubnet(String subnet) {
        Pattern pattern = Pattern.compile(SUBNET_PATTERN);
        Matcher matcher = pattern.matcher(subnet);

        return matcher.matches();
    }

    private String checkInputfileFormat(String[] args, String driveInputListFile)
            throws Exception {
        if (null == driveInputListFile) {
            throw new Exception("Missing input drives file path.");
        }

        if (hasArg("-format", args)) {
            String jsonFormatFlag = getArgValue("-format", args);
            if (jsonFormatFlag.equalsIgnoreCase("chassisjson")) {
                String driveInputListFile_temp = driveInputListFile + "_"
                        + "trans" + String.valueOf(System.currentTimeMillis());
                JsonConvertUtil.toJsonConverter(driveInputListFile,
                        driveInputListFile_temp, "chassis");
                driveInputListFile = driveInputListFile_temp;
            } else if (jsonFormatFlag.equalsIgnoreCase("racksjson")) {
                String driveInputListFile_temp = driveInputListFile + "_"
                        + "trans" + String.valueOf(System.currentTimeMillis());
                JsonConvertUtil.toJsonConverter(driveInputListFile,
                        driveInputListFile_temp, "hwview");
                driveInputListFile = driveInputListFile_temp;
            } else {
                throw new Exception("parameter error for format!");
            }
        }

        return driveInputListFile;
    }

}
