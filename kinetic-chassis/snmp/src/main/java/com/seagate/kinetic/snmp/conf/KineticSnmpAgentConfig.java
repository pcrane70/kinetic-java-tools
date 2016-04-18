package com.seagate.kinetic.snmp.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seagate.kinetic.snmp.KineticDevice;

public class KineticSnmpAgentConfig {
    private final static String AEGNG_SYS_DESC_OID_KEY = "agent.systemdescrption.oid";
    private final static String AEGNG_ITF_TABLE_OID_KEY = "agent.interfacestable.oid";
    private final static String AEGNG_SYS_DESC_KEY = "agent.systemdescrption";
    private final static String AEGNG_ADDRESS_KEY = "agent.address";
    private final static String AEGNG_KINETIC_MIB_PATH_KEY = "agent.kinetic.mib.path";
    private final static String AEG_CONFIG_HOME = "agent.conf.home";

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

    public static String getSystemDescriptionOid() {
        return properties.getProperty(AEGNG_SYS_DESC_OID_KEY,
                ".1.3.6.1.2.1.1.1.0");
    }

    public static String getInterfaceTableOid() {
        return properties.getProperty(AEGNG_ITF_TABLE_OID_KEY,
                ".1.3.6.1.2.1.2.2.1");
    }

    public static String getSystemDescription() {
        return properties.getProperty(AEGNG_SYS_DESC_KEY, "Kinetic");
    }

    public static String getAgentAddress() {
        return properties.getProperty(AEGNG_ADDRESS_KEY, "0.0.0.0/2001");
    }

    public static String getKineticMibFilePath() {
        String mibPathKey = properties.getProperty(AEGNG_KINETIC_MIB_PATH_KEY,
                "conf/mibs/kinetic.mib.json");

        return System.getProperty(AEG_CONFIG_HOME, ".") + File.separator
                + mibPathKey;
    }

    public static List<KineticDevice> loadKineticDevices() {
        List<KineticDevice> kineticDevices = new ArrayList<KineticDevice>();
        File file = new File(KineticSnmpAgentConfig.getKineticMibFilePath());
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
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
