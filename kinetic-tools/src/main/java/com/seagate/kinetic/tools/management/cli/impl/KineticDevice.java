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

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.common.util.MessageUtil;

public class KineticDevice {
    private List<String> inet4;
    private int port = 8123;
    private int tlsPort = 8443;
    private String wwn = "unknown";
    private String model = "unknown";
    private String serialNumber = "unknown";
    private String firmwareVersion = "unknown";

    public KineticDevice() {
        this.inet4 = new ArrayList<String>();
    }

    public KineticDevice(List<String> inet4, int port, int tlsPort, String wwn,
            String model, String serialNumber, String firmwareVersion) {
        this.inet4 = inet4;
        this.port = port;
        this.tlsPort = tlsPort;
        this.wwn = wwn;
        this.model = model;
        this.serialNumber = serialNumber;
        this.firmwareVersion = firmwareVersion;
    }

    public List<String> getInet4() {
        return inet4;
    }

    public void setInet4(List<String> inet4) {
        this.inet4 = inet4;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTlsPort() {
        return tlsPort;
    }

    public void setTlsPort(int tlsPort) {
        this.tlsPort = tlsPort;
    }

    public String getWwn() {
        return wwn;
    }

    public void setWwn(String wwn) {
        this.wwn = wwn;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public KineticDevice copy() {
        List<String> myInet4 = new ArrayList<String>();

        if (this.inet4 != null) {
            for (String i : inet4) {
                myInet4.add(i);
            }
        }

        return new KineticDevice(myInet4, this.port, this.tlsPort, this.wwn,
                this.model, this.serialNumber, this.firmwareVersion);
    }

    public static KineticDevice fromJson(String json) {
        return (KineticDevice) MessageUtil.fromJson(json, KineticDevice.class);
    }

    public static String toJson(KineticDevice device) {
        return MessageUtil.toJson(device);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (String e : inet4) {
            result = 31 * result + e.hashCode();
        }

        result = 31 * result + port;
        result = 31 * result + tlsPort;
        result = 31 * result + wwn.hashCode();
        result = 31 * result + model.hashCode();
        result = 31 * result + serialNumber.hashCode();
        result = 31 * result + firmwareVersion.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KineticDevice) || obj == null) {
            return false;
        }

        KineticDevice other = (KineticDevice) obj;
        return this.inet4.toString().equals(other.inet4.toString())
                && this.port == other.port && this.tlsPort == other.tlsPort
                && this.wwn.equals(other.wwn)
                && this.serialNumber.equals(other.serialNumber)
                && this.model.equals(other.model)
                && this.firmwareVersion.equals(other.firmwareVersion);
    }
}
