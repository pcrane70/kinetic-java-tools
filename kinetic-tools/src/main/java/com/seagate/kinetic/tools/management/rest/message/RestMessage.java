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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Rest message super class.
 * 
 * @author chiaming
 *
 */
public class RestMessage {

    private MessageType messageType = MessageType.PING;

    public void setMessageType(MessageType command) {
        this.messageType = command;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public String toJson() {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return this.toJson();
    }

}
