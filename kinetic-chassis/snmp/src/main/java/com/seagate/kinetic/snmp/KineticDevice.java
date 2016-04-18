package com.seagate.kinetic.snmp;

import java.util.ArrayList;
import java.util.List;

public class KineticDevice {
    private List<String> ips;
    private int port;
    private int tlsPort;
    private String wwn;

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public void setIps(String... ips) {
        this.ips = new ArrayList<String>();
        for (String ip : ips) {
            this.ips.add(ip);
        }
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
}
