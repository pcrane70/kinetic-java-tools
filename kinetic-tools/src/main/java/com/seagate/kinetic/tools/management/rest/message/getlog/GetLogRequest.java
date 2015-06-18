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

import kinetic.admin.KineticLogType;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;

/**
 * Get log request message.
 * 
 * @author chiaming
 */
public class GetLogRequest extends RestRequest {

    private KineticLogType type = null;

    /**
     * Vendor specific log name. The <code>KineticLogType</code> must be set to
     * DEVICE.
     */
    private String name = null;

    public GetLogRequest() {
        setMessageType(MessageType.GETLOG);
    }

    public void setLogType(KineticLogType type) {
        this.type = type;
    }

    public KineticLogType getLogType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
