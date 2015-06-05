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

import java.util.ArrayList;
import java.util.List;

/**
 * Rest response with device status.
 * 
 * @author chiaming
 *
 */
public class RestResponseWithStatus extends RestResponse {

    // list of device status
    protected List<DeviceStatus> devices = new ArrayList<DeviceStatus>();

    public void setDevices(List<DeviceStatus> devices) {
        this.devices = devices;
    }

    public List<DeviceStatus> getDevices() {
        return this.devices;
    }
}
