package com.seagate.kinetic.tools.management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class DeviceLoader 
{
	protected List<KineticDevice> devices = new ArrayList<KineticDevice>();
	
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
