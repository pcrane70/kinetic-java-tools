package com.seagate.kinetic.tools.management.cli.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class DefaultExecuter extends BasicSettings {
	protected List<KineticDevice> devices = new ArrayList<KineticDevice>();
	protected Map<KineticDevice, String> failed = new ConcurrentHashMap<KineticDevice, String>();
	protected Map<KineticDevice, String> succeed = new ConcurrentHashMap<KineticDevice, String>();

	protected void loadDevices(String drivesInputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				drivesInputFile));

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			devices.add(KineticDevice.fromJson(line));
		}

		reader.close();
	}
}
