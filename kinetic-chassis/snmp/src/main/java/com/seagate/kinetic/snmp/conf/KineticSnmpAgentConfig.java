package com.seagate.kinetic.snmp.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seagate.kinetic.snmp.KineticDevice;

public class KineticSnmpAgentConfig {

    public static final String AEG_CONFIG_HOME = "agent.conf.home";

    private String systemDescriptionOid = ".1.3.6.1.2.1.1.1.0";
    private String agentInterfaceTableOid = ".1.3.6.1.2.1.2.2.1";
    private String systemDescription = "Kinetic";
    private String host = "0.0.0.0";
    private int port = 2001;
    private String mibKineticPath = "conf/mibs/kinetic.mib1.json";

    public String getSystemDescriptionOid() {
        return systemDescriptionOid;
    }

    public void setSystemDescriptionOid(String systemDescriptionOid) {
        this.systemDescriptionOid = systemDescriptionOid;
    }

    public String getAgentInterfaceTableOid() {
        return agentInterfaceTableOid;
    }

    public void setAgentInterfaceTableOid(String agentInterfaceTableOid) {
        this.agentInterfaceTableOid = agentInterfaceTableOid;
    }

    public String getSystemDescription() {
        return systemDescription;
    }

    public void setSystemDescription(String systemDescription) {
        this.systemDescription = systemDescription;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMibKineticPath() {
        return System.getProperty(AEG_CONFIG_HOME, ".") + File.separator
                + mibKineticPath;
    }

    public void setMibKineticPath(String mibKineticPath) {
        this.mibKineticPath = mibKineticPath;
    }

    public List<KineticDevice> loadKineticDevices(String mibKienticAbPath) {
        List<KineticDevice> kineticDevices = new ArrayList<KineticDevice>();
        File file = new File(mibKienticAbPath);
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Mib.kinetic file isn't found");
        } catch (IOException e) {
            System.out.println("Reading Mib.kinetic file throws IOException!");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        String content = sb.toString();
        if (!content.isEmpty()) {
            kineticDevices = new Gson().fromJson(content,
                    new TypeToken<List<KineticDevice>>() {
                    }.getType());
        }
        return kineticDevices;
    }
}
