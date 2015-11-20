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
package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.ConnectionListener;
import kinetic.client.KineticException;

import com.seagate.kinetic.common.lib.KineticMessage;
import com.seagate.kinetic.proto.Kinetic.Command.GetLog.Configuration;
import com.seagate.kinetic.proto.Kinetic.Command.GetLog.Configuration.Interface;
import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.common.util.JsonConvertUtil;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.hwview.Chassis;
import com.seagate.kinetic.tools.management.rest.message.hwview.Coordinate;
import com.seagate.kinetic.tools.management.rest.message.hwview.Device;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareView;
import com.seagate.kinetic.tools.management.rest.message.hwview.Rack;
import com.seagate.kinetic.tools.management.rest.message.ping.PingResponse;

public class PingReachableDrive extends AbstractCommand {
    private static final String SUBNET_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final String SCOPE_IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    private static final int UNSOLICITED_MSG_MAX_WAIT_MS = 5000;
    private static final int SUB_NET_LENGTH = 255;
    private static final int TLS_PORT = 8443;
    private static final int PORT = 8123;
    private String driveListOutputFile;
    private String formatFlag = "";
    private String subnetPrefix = null;
    private int from = -1;
    private int to = -1;
    private AtomicInteger unSolicitedCounter = new AtomicInteger(0);
    private AtomicInteger reqCounter = new AtomicInteger(0);

    public PingReachableDrive(String subnetPrefixOrDriveInputFilePath,
            String driveListOutputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                subnetPrefixOrDriveInputFilePath);
        this.driveListOutputFile = driveListOutputFile;

        if (validateSubnet(subnetPrefixOrDriveInputFilePath)) {
            this.subnetPrefix = subnetPrefixOrDriveInputFilePath;
        }
    }

    public PingReachableDrive(String subnetPrefixOrDriveInputFilePath,
            String driveListOutputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout, String formatFlag) {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                subnetPrefixOrDriveInputFilePath);
        this.driveListOutputFile = driveListOutputFile;

        this.formatFlag = formatFlag;

        if (validateSubnet(subnetPrefixOrDriveInputFilePath)) {
            this.subnetPrefix = subnetPrefixOrDriveInputFilePath;
        }
    }

    public PingReachableDrive(String start, String end,
            String driveListOutputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout)
            throws KineticToolsException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, "");
        this.driveListOutputFile = driveListOutputFile;

        if (!validateScope(start, end)) {
            throw new KineticToolsException(
                    "Invalid start or end, they should set as an valid IP.");
        }

        String start_sub_24 = start.substring(0, start.lastIndexOf("."));
        String end_sub_24 = end.substring(0, end.lastIndexOf("."));
        if (!start_sub_24.equals(end_sub_24)) {
            throw new KineticToolsException(
                    "start and end should be in a same subnet(/24).");
        }

        this.subnetPrefix = start_sub_24;

        int tFrom = -1, tTo = -1;
        tFrom = Integer.parseInt(start.substring(start.lastIndexOf(".") + 1,
                start.length()));
        tTo = Integer.parseInt(end.substring(end.lastIndexOf(".") + 1,
                end.length()));

        if (tTo >= tFrom) {
            this.from = tFrom;
            this.to = tTo;
        } else {
            this.to = tFrom;
            this.from = tTo;
        }
    }

    public PingReachableDrive(List<DeviceId> deviceIds,
            String driveListOutputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, deviceIds);
        this.driveListOutputFile = driveListOutputFile;
    }

    private void waitUnSolicitedMesesages(int maxWaitMilliSeconds) {
        boolean stop = false;
        long start = System.currentTimeMillis();
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                if ((System.currentTimeMillis() - start > maxWaitMilliSeconds || unSolicitedCounter
                        .get() == 0) && reqCounter.get() == 0) {
                    stop = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!stop);

    }

    private void pingReachableDriveViaSubnet() throws Exception {
        UnSolicitedConnectionListener listener = new UnSolicitedConnectionListener();

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        List<String> inet4 = null;
        KineticDevice device = null;

        if (from == -1 || to == -1) {
            for (int i = 0; i < SUB_NET_LENGTH; i++) {
                inet4 = new ArrayList<String>();
                inet4.add(subnetPrefix + "." + i);
                device = new KineticDevice();
                device.setInet4(inet4);
                device.setPort(PORT);
                device.setTlsPort(TLS_PORT);

                threads.add(new PingReachableDriveThread(device, listener));
            }
        } else {
            for (int i = from; i <= to; i++) {
                inet4 = new ArrayList<String>();
                inet4.add(subnetPrefix + "." + i);
                device = new KineticDevice();
                device.setInet4(inet4);
                device.setPort(PORT);
                device.setTlsPort(TLS_PORT);

                threads.add(new PingReachableDriveThread(device, listener));
            }
        }

        poolExecuteThreadsInGroups(threads);

        waitUnSolicitedMesesages(UNSOLICITED_MSG_MAX_WAIT_MS);
    }

    private void pingReachableDriveViaDriveList() throws Exception {
        UnSolicitedConnectionListener listener = new UnSolicitedConnectionListener();

        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new PingReachableDriveThread(device, listener));
        }
        poolExecuteThreadsInGroups(threads);

        waitUnSolicitedMesesages(UNSOLICITED_MSG_MAX_WAIT_MS);
    }

    private String persistToFile(List<KineticDevice> deviceList,
            String filePath, String formatFlag) throws Exception {
        StringBuffer sb = new StringBuffer();

        if (formatFlag.equalsIgnoreCase("chassisjson") && deviceList != null
                && !deviceList.isEmpty() && deviceList.size() != 0) {
            List<Chassis> chassisOfList = generateChassisFromDeviceList(deviceList);

            JsonConvertUtil.fromJsonConverter(chassisOfList, filePath);
        } else if (formatFlag.equalsIgnoreCase("racksjson")
                && deviceList != null && !deviceList.isEmpty()
                && deviceList.size() != 0) {
            HardwareView hardwareView = new HardwareView();
            List<Rack> racks = new ArrayList<Rack>();
            Rack rack = new Rack();

            Coordinate coordinate = new Coordinate();
            coordinate.setX("rackx-0");
            coordinate.setY("racky-0");
            coordinate.setZ("rackz-0");

            List<Chassis> chassisOfList = generateChassisFromDeviceList(deviceList);

            rack.setId("1");
            rack.setCoordinate(coordinate);
            rack.setChassis(chassisOfList);

            racks.add(rack);

            hardwareView.setRacks(racks);

            JsonConvertUtil.fromJsonConverter(hardwareView, filePath);
        } else {
            assert (filePath != null);
            assert (deviceList != null);

            File file = new File(filePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            for (KineticDevice device : deviceList) {
                sb.append(KineticDevice.toJson(device));
                sb.append("\n");
            }
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        }

        return sb.toString();
    }

    private static List<Chassis> generateChassisFromDeviceList(
            List<KineticDevice> deviceList) {
        List<Chassis> chassisOfList = new ArrayList<Chassis>();

        Chassis chassis = new Chassis();
        Coordinate coordinateChassis = new Coordinate();
        coordinateChassis.setX("1");
        coordinateChassis.setY("chassisy-0");
        coordinateChassis.setZ("chassisz-0");

        List<Device> devices = new ArrayList<Device>();
        for (int index = 0; index < deviceList.size(); index++) {
            KineticDevice kineticDevice = new KineticDevice();
            kineticDevice = deviceList.get(index);

            if (null != kineticDevice) {
                Device device = new Device();
                Coordinate coordinateDevice = new Coordinate();
                coordinateDevice.setX("devicex-" + index);
                coordinateDevice.setY("devicey-" + index);
                coordinateDevice.setZ("devicez-" + index);

                DeviceId deviceId = new DeviceId();

                String[] ips = new String[2];
                if (kineticDevice.getInet4() != null
                        && !kineticDevice.getInet4().isEmpty()
                        && (2 >= kineticDevice.getInet4().size())) {
                    for (int i = 0; i < kineticDevice.getInet4().size(); i++) {
                        String ip = kineticDevice.getInet4().get(i);
                        if (null != ip) {
                            ips[i] = ip;
                        }
                    }
                }

                deviceId.setIps(ips);
                deviceId.setPort(kineticDevice.getPort());
                deviceId.setTlsPort(kineticDevice.getTlsPort());
                deviceId.setWwn(kineticDevice.getWwn());

                device.setDeviceId(deviceId);
                device.setCoordinate(coordinateDevice);

                devices.add(device);
            }
        }
        chassis.setDevices(devices);
        chassis.setCoordinate(coordinateChassis);
        chassis.setId("1");
        chassis.setIps(new String[] { "", "" });

        chassisOfList.add(chassis);

        return chassisOfList;
    }

    private boolean validateSubnet(String subnet) {
        Pattern pattern = Pattern.compile(SUBNET_PATTERN);
        Matcher matcher = pattern.matcher(subnet);

        return matcher.matches();
    }

    private boolean validateScope(String start, String end) {
        if (start == null || end == null)
            return false;

        Pattern pattern = Pattern.compile(SCOPE_IP_PATTERN);
        Matcher matcher1 = pattern.matcher(start);
        Matcher matcher2 = pattern.matcher(end);

        return matcher1.matches() && matcher2.matches();
    }

    class PingReachableDriveThread extends AbstractWorkThread {
        public PingReachableDriveThread(KineticDevice device,
                UnSolicitedConnectionListener listener) throws KineticException {
            super(device);
            adminClientConfig.setConnectionListener(listener);
        }

        @Override
        public void run() {
            try {
                adminClient = KineticAdminClientFactory
                        .createInstance(adminClientConfig);
                runTask();
            } catch (KineticException e) {
                report.reportFailure(device, null);
            } catch (KineticToolsException e) {
                report.reportFailure(device, null);
            } finally {
                reqCounter.getAndDecrement();
                try {
                    if (null != adminClient) {
                        adminClient.close();
                    }
                } catch (KineticException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }

        @Override
        void runTask() throws KineticToolsException {
            unSolicitedCounter.getAndIncrement();
        }
    }

    class UnSolicitedConnectionListener implements ConnectionListener {
        @Override
        public void onMessage(KineticMessage message) {
            KineticDevice device = new KineticDevice();
            Configuration configuration = null;
            if (null != message && null != message.getCommand()
                    && null != message.getCommand().getBody()
                    && null != message.getCommand().getBody().getGetLog())

                configuration = message.getCommand().getBody().getGetLog()
                        .getConfiguration();

            if (null != configuration) {
                List<Interface> itfs = configuration.getInterfaceList();
                List<String> inet4 = new ArrayList<String>();
                if (null != itfs && 0 != itfs.size()) {
                    if (null != itfs.get(0)
                            && null != itfs.get(0).getIpv4Address()) {
                        inet4.add(itfs.get(0).getIpv4Address().toStringUtf8());
                    }
                    if (null != itfs.get(1)
                            && null != itfs.get(1).getIpv4Address()) {
                        inet4.add(itfs.get(1).getIpv4Address().toStringUtf8());
                    }
                }
                device.setInet4(inet4);
                device.setModel(configuration.getModel());
                device.setPort(configuration.getPort());
                device.setTlsPort(configuration.getTlsPort());
                device.setFirmwareVersion(configuration.getVersion());
                device.setSerialNumber(configuration.getSerialNumber()
                        .toStringUtf8());
                device.setWwn(configuration.getWorldWideName().toStringUtf8());
            }

            report.reportSuccess(device, "");
            unSolicitedCounter.getAndDecrement();
        }
    }

    @Override
    public void init() throws KineticToolsException {
        if (null == subnetPrefix) {
            super.init();
            reqCounter = new AtomicInteger(devices.size());
        } else {
            if (from == -1 || to == -1) {
                reqCounter = new AtomicInteger(SUB_NET_LENGTH);
            } else {
                reqCounter = new AtomicInteger(to - from + 1);
            }
        }
    }

    @Override
    public void execute() throws KineticToolsException {
        if (null == subnetPrefix) {
            try {
                pingReachableDriveViaDriveList();
            } catch (Exception e) {
                throw new KineticToolsException(e);
            }
        } else {
            try {
                pingReachableDriveViaSubnet();
            } catch (Exception e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void done() throws KineticToolsException {
        List<KineticDevice> reachableDevices = report.getSucceedDevices();
        String toolHome = System.getProperty("kinetic.tools.out", ".");
        String rootDir = toolHome + File.separator + "out" + File.separator
                + driveListOutputFile;

        if (null == subnetPrefix) {
            super.done();
            try {
                PingResponse response = new PingResponse();
                report.persistReport(response, rootDir,
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (IOException e) {
                throw new KineticToolsException(e);
            }
        } else {
            if (reachableDevices.size() > 0) {
                System.out.println("\nDiscovered " + reachableDevices.size()
                        + " drives via subnet: " + subnetPrefix
                        + ", persist drives info in " + rootDir);
            }
            try {

                persistToFile(reachableDevices, rootDir, formatFlag);
            } catch (Exception e) {
                throw new KineticToolsException(e);
            }
        }
    }
}
