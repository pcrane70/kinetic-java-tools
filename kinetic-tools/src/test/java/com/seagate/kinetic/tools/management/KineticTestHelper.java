package com.seagate.kinetic.tools.management;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;
import com.seagate.kinetic.tools.management.common.util.MessageUtil;

public class KineticTestHelper {
    public final static String LOCAL_HOST = "127.0.0.1";
    public final static int PORT = 8123;
    public final static int SSL_PORT = 8443;
    private final static String FIRMWARE_VERSION = "2.6.0";
    private final static String UN_KNOWN = "unknown";
    private static final String TOOL_HOME = System.getProperty(
            "kinetic.tools.out", ".");
    private static final String ROOT_DIR = TOOL_HOME + File.separator + "out"
            + File.separator;
    public final static String FILE_NAME = "devicefortooltest.txt";

    public static void generateDeviceFile() throws IOException {
        KineticDevice kineticDevice = new KineticDevice();
        List<String> ipv4OfList = new ArrayList<String>();
        ipv4OfList.add(LOCAL_HOST);

        kineticDevice.setInet4(ipv4OfList);
        kineticDevice.setPort(PORT);
        kineticDevice.setTlsPort(SSL_PORT);
        kineticDevice.setFirmwareVersion(FIRMWARE_VERSION);
        kineticDevice.setModel(UN_KNOWN);
        kineticDevice.setSerialNumber(UN_KNOWN);
        kineticDevice.setWwn(UN_KNOWN);

        String kineticDevice2String = MessageUtil.toJson(kineticDevice);

        writeToDeviceFile(kineticDevice2String);

    }

    public static void removeDefaultDeviceFile() {
        File file = new File(ROOT_DIR + FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void removeDeviceFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    private static void writeToDeviceFile(String deviceJsonContent)
            throws IOException {
        File file = new File(ROOT_DIR + FILE_NAME);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(deviceJsonContent.getBytes(Charset.forName("UTF-8")));
        fos.flush();
        fos.close();
    }
}
