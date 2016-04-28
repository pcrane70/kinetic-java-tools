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
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.seagate.kinetic.heartbeat.HeartbeatMessage;
import com.seagate.kinetic.heartbeat.KineticNetworkInterface;
import com.seagate.kinetic.monitor.HeartbeatListener;
import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.common.util.JsonConvertUtil;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.hwview.Chassis;
import com.seagate.kinetic.tools.management.rest.message.hwview.Coordinate;
import com.seagate.kinetic.tools.management.rest.message.hwview.Device;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareView;
import com.seagate.kinetic.tools.management.rest.message.hwview.Rack;

public class DeviceDiscovery {
    private static final String SUBNET_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final String IP_ADDR_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    private static final int DEFAULT_MULTICAST_PORT = 8123;
    private static final int DEFAULT_IPV4_ARRAY_SIZE = 2;
    private Map<String, KineticDevice> devices = new ConcurrentHashMap<String, KineticDevice>();
    private static final String DEFAULT_MC_DESTNATION = "239.1.2.3";
    private boolean useListener = false;
    private String subnet = null;
    private int from = -1;
    private int to = -1;

    public DeviceDiscovery() throws Exception {
        startDiscoverNodes();
    }

    public DeviceDiscovery(String subnet) throws Exception {
        if (!validateSubnet(subnet))
            throw new KineticToolsException("Invalid subnet.");

        this.subnet = subnet;
        startDiscoverNodes();
    }

    public DeviceDiscovery(String start, String end) throws Exception {
        if (!validateScope(start, end))
            throw new KineticToolsException("Invalid startIp or endIp.");

        String start_subnet24 = start.substring(0, start.lastIndexOf("."));
        String end_subnet24 = end.substring(0, end.lastIndexOf("."));

        if (!start_subnet24.equals(end_subnet24))
            throw new KineticToolsException(
                    "startIp and endIp are not in a same subnet.");

        this.subnet = start_subnet24;

        int tFrom = lastPartOfIp(start);
        int tTo = lastPartOfIp(end);

        if (tFrom <= tTo) {
            this.from = tFrom;
            this.to = tTo;
        } else {
            this.from = tTo;
            this.to = tFrom;
        }

        startDiscoverNodes();
    }

    private int lastPartOfIp(String ip) {
        return Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1,
                ip.length()));
    }

    private boolean validateSubnet(String subnet) {
        if (subnet == null)
            return false;

        Pattern pattern = Pattern.compile(SUBNET_PATTERN);
        Matcher matcher = pattern.matcher(subnet);

        return matcher.matches();
    }

    private boolean validateScope(String start, String end) {
        if (start == null || end == null)
            return false;

        Pattern pattern = Pattern.compile(IP_ADDR_PATTERN);
        Matcher matcher1 = pattern.matcher(start);
        Matcher matcher2 = pattern.matcher(end);

        return matcher1.matches() && matcher2.matches();
    }

    public List<KineticDevice> listDevices() {
        List<KineticDevice> deviceList = new ArrayList<KineticDevice>();

        for (KineticDevice device : devices.values()) {
            deviceList.add(device.copy());
        }

        return deviceList;
    }

    public static String persistToFile(List<KineticDevice> deviceList,
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

    private void broadcastToDiscoverNodes() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface
                .getNetworkInterfaces();
        String mcastDestination = DEFAULT_MC_DESTNATION;
        int mcastPort = DEFAULT_MULTICAST_PORT;
        MulticastSocket multicastSocket;
        if (null != nets) {
            for (NetworkInterface netIf : Collections.list(nets)) {
                InetAddress iadd;
                try {
                    iadd = InetAddress.getByName(mcastDestination);
                    multicastSocket = new MulticastSocket(mcastPort);
                    multicastSocket.setNetworkInterface(netIf);
                    multicastSocket.joinGroup(iadd);
                } catch (Exception e1) {
                    continue;
                }
                new NodeDiscoveryThread(multicastSocket).start();
            }
        }
    }

    private void startDiscoverNodes() throws Exception {
        if (useListener) {
            new MyHeartbeatListener();

        } else {
            broadcastToDiscoverNodes();
        }
    }

    private void addToDeviceList(KineticDevice device, String key) {
        if (!devices.containsKey(key)) {
            // not subnet and not scope
            if (subnet == null) {
                devices.put(device.toString(), device);
            } else {
                int lastPartOfIP = -1;
                for (String ip : device.getInet4()) {
                    if (ip.indexOf(subnet) == 0) {
                        // scope
                        if (from != -1 && to != -1) {
                            lastPartOfIP = lastPartOfIp(ip);
                            if (lastPartOfIP >= from && lastPartOfIP <= to) {
                                devices.put(device.toString(), device);
                                return;
                            }
                        }
                        // subnet
                        else if (from == -1 && to == -1) {
                            devices.put(device.toString(), device);
                            return;
                        }
                    }
                }
            }
        }
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

                String[] ips = new String[DEFAULT_IPV4_ARRAY_SIZE];
                if (kineticDevice.getInet4() != null
                        && !kineticDevice.getInet4().isEmpty()) {
                    int size = kineticDevice.getInet4().size();
                    ips = new String[size];
                    for (int i = 0; i < size; i++) {
                        ips[i] = kineticDevice.getInet4().get(i);
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

    class MyHeartbeatListener extends HeartbeatListener {

        public MyHeartbeatListener() throws IOException {
            super();
        }

        public void onMessage(byte[] data) {
            HeartbeatMessage msg = HeartbeatMessage.fromJson(new String(data)
                    .trim());
            List<KineticNetworkInterface> networkItfs = msg
                    .getNetworkInterfaces();
            int port = msg.getPort();
            int tlsPort = msg.getTlsPort();
            String wwn = msg.getWorldWideName();
            String model = msg.getModel();
            String serialNumber = msg.getSerialNumber();
            String firmwareVersion = msg.getFirmwareVersion();

            List<String> inet4 = new ArrayList<String>();

            for (int i = 0; i < networkItfs.size(); i++) {
                inet4.add(networkItfs.get(i).getIpV4Address());
            }

            KineticDevice device = new KineticDevice(inet4, port, tlsPort, wwn,
                    model, serialNumber, firmwareVersion);
            addToDeviceList(device, device.toString());
        }
    }

    class NodeDiscoveryThread extends Thread {
        private static final int MAX_DATAGRAM_PACKET_SIZE = 64 * 1024;
        private static final String FIRMWARE_VERSION = "firmware_version";
        private static final String SERIAL_NUMBER = "serial_number";
        private static final String TLS_PORT = "tlsPort";
        private static final String PORT = "port";
        private static final String IPV4_ADDR = "ipv4_addr";
        private static final String NETWORK_INTERFACES = "network_interfaces";
        private static final String WORLD_WIDE_NAME = "world_wide_name";
        private static final String DRIVE_MODEL = "model";
        private MulticastSocket multicastSocket;

        public NodeDiscoveryThread(MulticastSocket multicastSocket) {
            this.multicastSocket = multicastSocket;
        }

        public List<KineticDevice> registerNewDiscoveredNodes()
                throws Exception {
            List<KineticDevice> newDiscoveredNodes = new ArrayList<KineticDevice>();

            byte[] b = new byte[MAX_DATAGRAM_PACKET_SIZE];
            DatagramPacket p = new DatagramPacket(b, b.length);
            multicastSocket.receive(p);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(p.getData(), JsonNode.class);

            if (!validateRoot(root)) {
                return newDiscoveredNodes;
            }

            String model = root.get(DRIVE_MODEL).asText();

            String wwn = root.get(WORLD_WIDE_NAME).asText();
            if (devices.containsKey(wwn))
                return newDiscoveredNodes;

            JsonNode ifs = root.get(NETWORK_INTERFACES);
            List<String> inet4 = new ArrayList<String>();
            if (!ifs.isArray()) {
                return newDiscoveredNodes;
            } else {
                for (int i = 0; i < ifs.size(); i++) {
                    inet4.add(ifs.get(i).get(IPV4_ADDR).asText());
                }
            }

            KineticDevice node = new KineticDevice(inet4, root.get(PORT)
                    .asInt(), root.get(TLS_PORT).asInt(), wwn, model, root.get(
                    SERIAL_NUMBER).asText(), root.get(FIRMWARE_VERSION)
                    .asText());

            // devices.put(wwn, node);
            addToDeviceList(node, wwn);
            newDiscoveredNodes.add(node);

            return newDiscoveredNodes;
        }

        private boolean validateRoot(JsonNode root) {
            if (null == root) {
                return false;
            }

            if (null == root.get(DRIVE_MODEL)
                    || null == root.get(WORLD_WIDE_NAME)
                    || null == root.get(NETWORK_INTERFACES)
                    || null == root.get(PORT) || null == root.get(TLS_PORT)
                    || null == root.get(SERIAL_NUMBER)
                    || null == root.get(FIRMWARE_VERSION)) {
                return false;
            }

            return true;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    registerNewDiscoveredNodes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
