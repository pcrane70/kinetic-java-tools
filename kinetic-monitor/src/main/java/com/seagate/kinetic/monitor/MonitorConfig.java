/**
 * 
 * Copyright (C) 2014 Seagate Technology.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.seagate.kinetic.monitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class MonitorConfig {
	private static final String DEFAULT_REST_SERVICE_URL = "127.0.0.1:8080";
	public static final int DEFAULT_DATA_COLLECT_INTERVAL = 1;
	public static final String DEFAULT_NODE_DISCOVER_METHOD = "broadcast";
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

	public static String getRestServiceUrl() throws Exception {
		checkProp();

		return prop.getProperty("restServiceUrl", DEFAULT_REST_SERVICE_URL);
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
		System.out.println(MonitorConfig.getRestServiceUrl());
	}

}
