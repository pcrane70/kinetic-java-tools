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
public class GetlogRequest extends RestRequest {

    private KineticLogType logType = null;

    private boolean useSsl = false;

    public void setUseSsl(boolean flag) {
        this.useSsl = flag;
    }

    public boolean getUseSsl() {
        return this.useSsl;
    }

    public void setLogType(KineticLogType type) {
        this.logType = type;
    }

    public KineticLogType getLogType() {
        return this.logType;
    }

    public GetlogRequest() {
        setMessageType(MessageType.GETLOG);
    }
}
