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

import java.util.List;

/**
 * Rest request message super class.
 * 
 * @author chiaming
 *
 */
public class RestRequest extends RestMessage {

    private String identity = "1";

    private String key = "asdfasdf";

    private boolean useSsl = false;

    // cluster version
    private int clversion = 0;

    // request timeout in seconds
    private int reqtimeout = 30;

    /**
     * The discover Id is returned in the DiscoverResponse message. If this is
     * present in the subsequent requests, all devices associated with the
     * cached discover result will be included in the request.
     * 
     * If this is present, the devices field is ignored.
     */
    private String discoid = null;

    private List<DeviceId> devices = null;

    public void setIdentity(String id) {
        this.identity = id;
    }

    public String getIdentity() {
        return this.identity;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public boolean getUseSsl() {
        return this.useSsl;
    }

    public void setRequestTimeout(int requestTimeoutInSeconds) {
        this.reqtimeout = requestTimeoutInSeconds;
    }

    public int getRequestTimeout() {
        return this.reqtimeout;
    }

    public void setDevices(List<DeviceId> devices) {
        this.devices = devices;
    }

    public List<DeviceId> getDevices() {
        return this.devices;
    }

    public String getDiscoId() {
        return this.discoid;
    }

    public void setDiscoId(String id) {
        this.discoid = id;
    }

    public void setClversion(int v) {
        this.clversion = v;
    }

    public int getClversion() {
        return this.clversion;
    }

}
