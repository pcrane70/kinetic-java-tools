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
package com.seagate.kinetic.tools.external;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;

/**
 * external request message.
 * 
 * @author chiaming
 */
public class ExternalRequest extends RestRequest {

	// make all fields optional and let downstream code to valiate the 
	// parameters
    private String msg = null;
    transient private String dir = null;
    transient private String user = null;
    transient private String key = null;
    transient private String file = null;

    public ExternalRequest() {
        setMessageType(MessageType.EXTERNAL_REQUEST);
    }

    public void setRequestMessage(String msg) {
        this.msg = msg;
    }

    public String getRequestMessage() {
        return this.msg;
    }
    
    public String getDir() { return this.dir; }
    public void setDir(String dir) { this.dir = dir; }
    public String getUser() { return this.user; }
    public void setUser(String user) { this.user = user; }
    public String getKey() { return this.key; }
    public void setKey(String key) { this.key = key; }
    public String getFile() { return this.file; }
    public void setFile(String file) { this.file = file; }

}
