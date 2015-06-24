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
package com.seagate.kinetic.tools.management.rest.message.firmware;

import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestMessage;

/**
 * Get log request message.
 * 
 * @author chiaming
 */
public class GetFirmwareResponse extends RestMessage {

    private List<String> firmwareList = null;
    
    public GetFirmwareResponse() {
        super.setMessageType(MessageType.GET_FIRMWARE_REPLY);
    }

    public void setFirmwareList(List<String> firmwareList) {
        this.firmwareList = firmwareList;
    }

    public List<String> getFirmwareList() {
        return this.firmwareList;
    }
}
