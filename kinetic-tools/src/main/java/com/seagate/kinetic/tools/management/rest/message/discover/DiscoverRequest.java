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

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;

/**
 * Discover request message.
 * 
 * @author chiaming
 */
public class DiscoverRequest extends RestRequest {

    private String subnet = null;

    private int timeout = 10;

    private boolean scoped = false;

    private String startIp = null;

    private String endIp = null;

    public DiscoverRequest() {
        setMessageType(MessageType.DISCOVER);
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public String getSubnet() {
        return this.subnet;
    }

    public void setTimeout(int timeoutInSeconds) {
        this.timeout = timeoutInSeconds;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setScoped(boolean scoped) {
        this.scoped = scoped;
    }

    public boolean getScoped() {
        return this.scoped;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getStartIp() {
        return this.startIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getEndIp() {
        return this.endIp;
    }

}
