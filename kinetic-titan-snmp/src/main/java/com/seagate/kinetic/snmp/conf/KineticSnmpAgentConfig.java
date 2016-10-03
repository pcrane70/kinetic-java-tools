package com.seagate.kinetic.snmp.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class KineticSnmpAgentConfig {
    private final static String AEGNG_ADDRESS_KEY = "agent.address";
    private final static String AEG_CONFIG_HOME = "agent.conf.home";
    private final static String PLANE_A_PREFIX = "mo.plane.a.value.1.3.6.1.4.1.3581.12.7.2.1.1.10";
    private final static String PLANE_B_PREFIX = "mo.plane.b.value.1.3.6.1.4.1.3581.12.7.2.1.1.11";
    private final static String MO_IP_OID_ROOT = "mo.ip.oid.root";

    private static Properties properties;

    static {
        properties = new Properties();
        try {
            String home = System.getProperty(AEG_CONFIG_HOME, ".");
            properties.load(new FileInputStream(new File(home + File.separator
                    + "conf/agent.config")));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getIpOidRoot() {
        return properties.getProperty(MO_IP_OID_ROOT,
                "1.3.6.1.4.1.3581.12.7.2.1.1");
    }

    public static String getIpOfPlaneA(int postfix) {
        return properties
                .getProperty(PLANE_A_PREFIX + "." + postfix, "unknown");
    }

    public static String getIpOfPlaneB(int postfix) {
        return properties
                .getProperty(PLANE_B_PREFIX + "." + postfix, "unknown");
    }

    public static String getAgentAddress() {
        return properties.getProperty(AEGNG_ADDRESS_KEY, "0.0.0.0/2001");
    }
}
