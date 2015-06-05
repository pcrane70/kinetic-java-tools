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
package com.seagate.kinetic.tools.management.rest.message.discover;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceInfo;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;

/**
 * Discover response message.
 * 
 * @author chiaming
 *
 */
public class DiscoverResponse extends RestResponse {

    /**
     * The discover Id is returned in the DiscoverResponse message. If this is
     * present in the subsequent requests, all devices associated with the
     * cached discover result will be associated with the request.
     */
    private String discoid = null;

    protected List<DeviceInfo> devices = new ArrayList<DeviceInfo>();

    public DiscoverResponse() {
        setMessageType(MessageType.DISCOVER_REPLY);
    }

    public String getDiscoId() {
        return this.discoid;
    }

    public void setDiscoId(String id) {
        this.discoid = id;
    }

    public void setDevices(List<DeviceInfo> devices) {
        this.devices = devices;
    }

    public List<DeviceInfo> getDevices() {
        return this.devices;
    }
}
