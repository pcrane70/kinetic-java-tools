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

import javax.servlet.http.HttpServletResponse;

import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;

/**
 * 
 * Json container that contains device heartbeat data and status.
 * 
 * @author chiaming
 *
 */
public class DeviceInfo {

    private KineticDevice device = null;

    private int status = HttpServletResponse.SC_OK;

    private String message = null;

    public void setDevice(KineticDevice device) {
        this.device = device;
    }

    public KineticDevice getDevice() {
        return this.device;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
