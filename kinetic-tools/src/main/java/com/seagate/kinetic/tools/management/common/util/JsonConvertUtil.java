package com.seagate.kinetic.tools.management.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.seagate.kinetic.tools.management.cli.impl.KineticDevice;
import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.hwview.Chassis;
import com.seagate.kinetic.tools.management.rest.message.hwview.Coordinate;
import com.seagate.kinetic.tools.management.rest.message.hwview.Device;

public class JsonConvertUtil {

    public static void fromJsonConverter(List<Chassis> chassis, String jsonFileOutPath)
            throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(chassis);

        persistToFile(json, jsonFileOutPath);

    }

    public static String toJsonConverter(String jsonFileInPath,
            String jsonFileOutPath) throws Exception {
        String jsonContent = jsonFileReader(jsonFileInPath);
        if (null == jsonContent) {
            throw new KineticToolsException("Json fie is null!");
        }

        List<KineticDevice> deviceOfList = new ArrayList<KineticDevice>();

        JsonElement jElement = new JsonParser().parse(jsonContent);
        if (jElement.isJsonNull()) {
            throw new KineticToolsException(
                    "Json element which gets from json file is null!");
        }

        JsonObject jObject = jElement.getAsJsonObject();
        if (jObject.isJsonNull()) {
            throw new KineticToolsException(
                    "Json object which gets from json file is null!");
        }

        JsonArray chassisArray = jObject.getAsJsonArray("chassis");
        if (chassisArray.isJsonNull()) {
            throw new KineticToolsException(
                    "Chassis which gets from json file is null!");
        }

        for (int index = 0; index < chassisArray.size(); index++) {
            if (null != chassisArray.get(index)) {
                JsonObject chassisObject = chassisArray.get(index)
                        .getAsJsonObject();
                if (!chassisObject.isJsonNull() && chassisObject.isJsonObject()) {
                    JsonArray deviceArray = chassisObject
                            .getAsJsonArray("devices");
                    for (int i = 0; i < deviceArray.size(); i++) {
                        if (null != deviceArray.get(i)
                                && !deviceArray.get(i).getAsJsonObject()
                                        .isJsonNull()) {
                            JsonObject deviceObj = deviceArray.get(i)
                                    .getAsJsonObject();
                            JsonObject deviceId = deviceObj
                                    .getAsJsonObject("deviceId");
                            if (!deviceId.isJsonNull()
                                    && deviceId.isJsonObject()) {
                                KineticDevice device = new KineticDevice();
                                device.setWwn(deviceId.get("wwn").getAsString());
                                device.setPort(deviceId.get("port").getAsInt());
                                device.setTlsPort(deviceId.get("tlsPort")
                                        .getAsInt());

                                JsonArray ips = deviceId.getAsJsonArray("ips");

                                List<String> ipOfList = new ArrayList<String>();
                                if (null != ips) {
                                    for (int j = 0; j < ips.size(); j++) {
                                        if (null != ips.get(j)) {
                                            ipOfList.add(ips.get(j)
                                                    .getAsString());
                                        }
                                    }
                                    device.setInet4(ipOfList);
                                }
                                deviceOfList.add(device);
                            }
                        }
                    }
                }
            }
        }

        return persistToFile(deviceOfList, jsonFileOutPath);
    }

    private static String jsonFileReader(String jsonFilePath)
            throws KineticToolsException, IOException {
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new KineticToolsException("Json file is not existed in: "
                    + jsonFilePath);
        }

        StringBuffer sb = new StringBuffer();

        BufferedReader in = new BufferedReader(new FileReader(jsonFilePath));
        String str;
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }

        in.close();

        return sb.toString();
    }

    private static String persistToFile(List<KineticDevice> deviceList,
            String filePath) throws Exception {
        assert (filePath != null);
        assert (deviceList != null);

        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(file);
        StringBuffer sb = new StringBuffer();
        for (KineticDevice device : deviceList) {
            sb.append(KineticDevice.toJson(device));
            sb.append("\n");
        }
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();

        return sb.toString();
    }

    private static String persistToFile(String str, String filePath)
            throws Exception {
        if (filePath == null) {
            throw new KineticToolsException("file path is null");
        }

        if (str == null) {
            throw new KineticToolsException("chassis is null");
        }

        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(file);
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"chassis\":");
        sb.append(str);
        sb.append("}");

        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        // toJsonConverter(
        // "/Users/Emma/git/kinetic-java-tools/kinetic-tools/src/main/java/com/seagate/kinetic/tools/management/common/util/test_titan.json",
        // "/Users/Emma/git/kinetic-java-tools/kinetic-tools/src/main/java/com/seagate/kinetic/tools/management/common/util/discover-test-1.json");
        Chassis chassis = new Chassis();
        chassis.setId("titan");
        String[] ips = new String[] { "mgmt-ip1", "mgmt-ip2" };
        chassis.setIps(ips);

        DeviceId deviceId = new DeviceId();
        deviceId.setIps(new String[] { "data-ip-1", "data-ip-2" });
        deviceId.setPort(8123);
        deviceId.setTlsPort(8443);
        deviceId.setWwn("wwn-0");
        Coordinate coordinate = new Coordinate();
        coordinate.setX("x");
        coordinate.setY("y");
        coordinate.setZ("z");

        Device device1 = new Device();
        device1.setDeviceId(deviceId);
        device1.setCoordinate(coordinate);

        List<Device> devices = new ArrayList<Device>();
        devices.add(device1);

        chassis.setDevices(devices);
        chassis.setCoordinate(coordinate);

//        fromJsonConverter(
//                chassis,
//                "/Users/Emma/git/kinetic-java-tools/kinetic-tools/src/main/java/com/seagate/kinetic/tools/management/common/util/json_trans.json");

    }

}
