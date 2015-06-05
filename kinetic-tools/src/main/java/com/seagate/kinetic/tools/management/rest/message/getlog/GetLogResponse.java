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
package com.seagate.kinetic.tools.management.rest.message.getlog;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;

/**
 * Get log response message.
 * 
 * @author chiaming
 *
 */
public class GetLogResponse extends RestResponse {

    private List<DeviceLog> deviceLogs = new ArrayList<DeviceLog>();

    // vendor specific device log
    private byte[] value = null;

    public GetLogResponse() {
        setMessageType(MessageType.GETLOG_REPLY);
    }

    public void setDeviceLogs(List<DeviceLog> dlogs) {
        this.deviceLogs = dlogs;
    }

    public List<DeviceLog> getDeviceLogs() {
        return this.deviceLogs;
    }

    /**
     * XXX chiaming 06/04/2015: only support one device at a time.
     * 
     * @param value
     *            vendor specific device log
     */
    public void setValue(byte[] value) {
        this.value = value;
    }

    /**
     * get vendor specific device log.
     * 
     * @return vendor specific device log
     */
    public byte[] getValue() {
        return this.value;
    }

}
