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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kinetic.admin.KineticAdminClient;

public class AdminClientRegister {
    private Map<String, KineticAdminClient> adminClientRegMap;

    public AdminClientRegister() {
        adminClientRegMap = new ConcurrentHashMap<String, KineticAdminClient>();
    }

    public void register(String device, KineticAdminClient adminClient) {
        if (null == device) {
            return;
        }
        adminClientRegMap.put(device, adminClient);
    }

    public void deRegister(String device) {
        if (null == device) {
            return;
        }
        adminClientRegMap.remove(device);
    }

    public KineticAdminClient getKineticAdminClient(String device) {
        return device == null ? null : adminClientRegMap.get(device);
    }
}