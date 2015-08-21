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
package com.seagate.kinetic.tools.external.swift;

import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.management.rest.message.MessageType;

/**
 * Swift specific external request message.
 * 
 * @author chiaming
 * @author Shafig
 */
public class SwiftRequest extends ExternalRequest {

	// make all fields optional and let downstream code to valiate the 
	// parameters
    private String resource = null;
    private String dir = null;
    private String user = null;
    private String swiftKey = null;
    private String file = null;
    private String partition = null;
    private String url = null;
    private String command = null;
    private String password = null;
    private String host = null;
    

    public SwiftRequest() {
        setMessageType(MessageType.EXTERNAL_REQUEST);
    }

    public String getResource() { return this.resource; }

    public void setResource(String resource) { this.resource = resource; }
    
    public String getDir() { return this.dir; }

    public void setDir(String dir) { this.dir = dir; }

    public String getUser() { return this.user; }

    public void setUser(String user) { this.user = user; }

    public String getSwiftKey() { return this.swiftKey; }

    public void setSwiftKey(String val) { this.swiftKey = val; }

    public String getFile() { return this.file; }

    public void setFile(String file) { this.file = file; }

    public String getPartition() { return this.partition; }

    public void setPartition(String partition) { this.partition = partition; }

    public String getUrl() { return this.url; }

    public void setUrl(String file) {
        this.url = file;
    }

    public String getCommand() { return this.command; }

    public void setCommand(String command) { this.command = command; }
    
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }
    public String getHost() { return this.host; }
    public void setHost(String host) { this.host = host; }


}
