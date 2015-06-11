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
