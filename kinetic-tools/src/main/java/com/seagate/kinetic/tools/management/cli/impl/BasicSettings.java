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
package com.seagate.kinetic.tools.management.cli.impl;

class BasicSettings {
    private boolean useSsl = false;
    private long clusterVersion = 0;
    private long identity = 1;
    private String key = "asdfasdf";
    private long requestTimeout = 10;
    private String drivesLogFile = null;

    public boolean isUseSsl() {
        return useSsl;
    }

    public BasicSettings setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
        return this;
    }

    public long getClusterVersion() {
        return clusterVersion;
    }

    public BasicSettings setClusterVersion(long clusterVersion) {
        this.clusterVersion = clusterVersion;
        return this;
    }

    public long getIdentity() {
        return identity;
    }

    public BasicSettings setIdentity(long identity) {
        this.identity = identity;
        return this;
    }

    public String getKey() {
        return key;
    }

    public BasicSettings setKey(String key) {
        this.key = key;
        return this;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public BasicSettings setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public String getDrivesLogFile() {
        return drivesLogFile;
    }

    public BasicSettings setDrivesLogFile(String drivesLogFile) {
        this.drivesLogFile = drivesLogFile;
        return this;
    }
}
