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
package com.seagate.kinetic.tools.management.rest.message;

import java.util.List;

/**
 * Rest request message super class.
 * 
 * @author chiaming
 *
 */
public class RestRequest extends RestMessage {

    private String identity = "1";

    private String key = "asdfasdf";

    private List<DeviceId> devices = null;

    public void setIdentity(String id) {
        this.identity = id;
    }

    public String getIdentity() {
        return this.identity;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setDevices(List<DeviceId> devices) {
        this.devices = devices;
    }

    public List<DeviceId> getDevices() {
        return this.devices;
    }

}
