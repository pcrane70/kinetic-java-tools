package com.seagate.kinetic.tools.management;

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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.seagate.kinetic.heartbeat.HeartbeatMessage;
import com.seagate.kinetic.heartbeat.KineticNetworkInterface;
import com.seagate.kinetic.monitor.HeartbeatListener;

public class DeviceDiscovery {
    private Map<String, KineticDevice> devices = new ConcurrentHashMap<String, KineticDevice>();
    private static final String DEFAULT_MC_DESTNATION = "239.1.2.3";
    private boolean useListener = false;

    public DeviceDiscovery() throws Exception {
        startDiscoverNodes();
    }

    public List<KineticDevice> listDevices() {
        List<KineticDevice> deviceList = new ArrayList<KineticDevice>();

        for (KineticDevice device : devices.values()) {
            deviceList.add(device.copy());
        }

        return deviceList;
    }

    public static String persistToFile(List<KineticDevice> deviceList,
            String filePath) throws Exception {
        assert (filePath != null);
        assert (deviceList != null);

        FileOutputStream fos = new FileOutputStream(new File(filePath));
        StringBuffer sb = new StringBuffer();
        for (KineticDevice device : deviceList) {
            sb.append(KineticDevice.toJson(device));
            sb.append("\n");
        }
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();

        return sb.toString();
    }

    private void broadcastToDiscoverNodes() throws IOException {
        Enumeration<NetworkInterface> nets = NetworkInterface
                .getNetworkInterfaces();
        String mcastDestination = DEFAULT_MC_DESTNATION;
        int mcastPort = 8123;
        MulticastSocket multicastSocket;
        for (NetworkInterface netIf : Collections.list(nets)) {
            InetAddress iadd;
            iadd = InetAddress.getByName(mcastDestination);
            multicastSocket = new MulticastSocket(mcastPort);
            try {
                multicastSocket.setNetworkInterface(netIf);
            } catch (SocketException e) {
                continue;
            }
            multicastSocket.joinGroup(iadd);
            new NodeDiscoveryThread(multicastSocket).start();
        }
    }

    private void startDiscoverNodes() throws Exception {
        if (useListener) {
            new MyHeartbeatListener();

        } else {
            broadcastToDiscoverNodes();
        }
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
            if (!devices.containsKey(device.toString())) {
                devices.put(device.toString(), device);
            }
        }

    }

    class NodeDiscoveryThread extends Thread {
        private MulticastSocket multicastSocket;

        public NodeDiscoveryThread(MulticastSocket multicastSocket) {
            this.multicastSocket = multicastSocket;
        }

        public List<KineticDevice> registerNewDiscoveredNodes()
                throws Exception {
            List<KineticDevice> newDiscoveredNodes = new ArrayList<KineticDevice>();

            byte[] b = new byte[64 * 1024];
            DatagramPacket p = new DatagramPacket(b, b.length);
            multicastSocket.receive(p);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(p.getData(), JsonNode.class);

            String model = root.get("model").asText();

            String wwn = root.get("world_wide_name").asText();
            if (devices.containsKey(wwn))
                return newDiscoveredNodes;

            JsonNode ifs = root.get("network_interfaces");
            List<String> inet4 = new ArrayList<String>();
            if (!ifs.isArray()) {
                return newDiscoveredNodes;
            } else {
                for (int i = 0; i < ifs.size(); i++) {
                    inet4.add(ifs.get(i).get("ipv4_addr").asText());
                }
            }

            KineticDevice node = new KineticDevice(inet4, root.get("port")
                    .asInt(), root.get("tlsPort").asInt(), wwn, model, root
                    .get("serial_number").asText(), root
                    .get("firmware_version").asText());

            devices.put(wwn, node);
            newDiscoveredNodes.add(node);

            return newDiscoveredNodes;
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
