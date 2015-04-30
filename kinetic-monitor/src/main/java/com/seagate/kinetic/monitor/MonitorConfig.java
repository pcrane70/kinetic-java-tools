package com.seagate.kinetic.monitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class MonitorConfig {
	public static final int DEFAULT_DATA_COLLECT_INTERVAL = 1;
	public static final String DEFAULT_NODE_DISCOVER_METHOD = "listener";
	private static final String configFilePath = "conf/monitor.conf";
	private static Properties prop = new Properties();

	static {
		try {
			prop.load(new FileInputStream(configFilePath));
		} catch (FileNotFoundException e) {
			prop = null;
			e.printStackTrace();
		} catch (IOException e) {
			prop = null;
			e.printStackTrace();
		}
	}

	public static List<String> listMonitorNodes() throws Exception {
		checkProp();

		String nodesStr = prop.getProperty("nodes");
		List<String> nodesList = new ArrayList<String>();

		if (nodesStr.isEmpty()) {
			return nodesList;
		}

		String ipAndPortArray[] = nodesStr.split(";");

		for (String ipAndPort : ipAndPortArray) {
			parseIpAndPortRange(nodesList, ipAndPort);

		}

		return nodesList;
	}

	public static String getNodeDiscoveryMethod() throws Exception {
		checkProp();

		return prop.getProperty("nodeDiscoverMethod",
				DEFAULT_NODE_DISCOVER_METHOD);
	}

	public static int getDataCollectInterval() throws Exception {
		checkProp();
		return Integer.parseInt(prop.getProperty("dataCollectInterval",
				DEFAULT_DATA_COLLECT_INTERVAL + ""));
	}

	private static void parseIpAndPortRange(List<String> nodesList,
			String ipAndPort) throws Exception {
		String[] temp;
		String ips;
		String ports;
		int ipStart;
		int ipEnd;
		int portStart;
		int portEnd;
		boolean hasPortRange;
		boolean hasIpRange;
		temp = ipAndPort.split(":");
		assert (temp.length == 2);

		ips = temp[0];
		ports = temp[1];
		hasIpRange = (ips.indexOf("~") != -1);
		hasPortRange = (ports.indexOf("~") != -1);

		if (hasIpRange && hasPortRange) {
			throw new Exception("Wrong nodes configuration");
		} else if (hasIpRange) {
			temp = ips.split("~");
			assert (temp.length == 2);

			ipStart = Integer.parseInt(temp[0].substring(temp[0]
					.lastIndexOf(".") + 1));
			ipEnd = Integer.parseInt(temp[1]);

			for (int i = ipStart; i <= ipEnd; i++) {
				nodesList.add(temp[0].substring(0, temp[0].lastIndexOf("."))
						+ "." + i + ":" + ports);
			}
		} else if (hasPortRange) {
			temp = ports.split("~");
			assert (temp.length == 2);

			portStart = Integer.parseInt(temp[0]);
			portEnd = Integer.parseInt(temp[1]);

			for (int i = portStart; i <= portEnd; i++) {
				nodesList.add(ips + ":" + i);
			}
		} else {
			nodesList.add(ips + ":" + ports);
		}
	}

	private static void checkProp() throws Exception {
		if (prop == null) {
			throw new Exception("Load  conf/monitor.conf failed.");
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(MonitorConfig.listMonitorNodes().toString());
		System.out.println(MonitorConfig.getDataCollectInterval());
		System.out.println(MonitorConfig.getNodeDiscoveryMethod());
	}

}
