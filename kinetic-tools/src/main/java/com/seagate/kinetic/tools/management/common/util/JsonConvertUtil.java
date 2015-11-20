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
import com.seagate.kinetic.tools.management.rest.message.hwview.Chassis;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareView;

public class JsonConvertUtil {

    public static void fromJsonConverter(List<Chassis> chassis,
            String jsonFileOutPath) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(chassis);

        persistToFile(json, jsonFileOutPath, "chassis");

    }

    public static void fromJsonConverter(HardwareView hardwareView,
            String jsonFileOutPath) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(hardwareView);

        persistToFile(json, jsonFileOutPath, "hwview");

    }

    public static String toJsonConverter(String jsonFileInPath,
            String jsonFileOutPath, String formatFlag) throws Exception {
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

        if (formatFlag.equalsIgnoreCase("chassis")) {
            JsonArray chassisArray = jObject.getAsJsonArray("chassis");
            deviceOfList = parseChassisFromJsonObject(chassisArray);

        } else if (formatFlag.equalsIgnoreCase("hwview")) {
            JsonArray rackArray = jObject.getAsJsonArray("racks");
            if (rackArray.isJsonNull()) {
                throw new KineticToolsException(
                        "Racks which gets from json file is null!");
            }

            for (int rackIndex = 0; rackIndex < rackArray.size(); rackIndex++) {
                if (null != rackArray.get(rackIndex)) {
                    JsonObject rackObject = rackArray.get(rackIndex)
                            .getAsJsonObject();
                    JsonArray chassisArray = rackObject
                            .getAsJsonArray("chassis");

                    deviceOfList = parseChassisFromJsonObject(chassisArray);
                }
            }
        } else {
            throw new KineticToolsException("Json format flag (" + formatFlag
                    + ") is not supported. The valid is chassis or hwview");
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

    private static String persistToFile(String str, String filePath,
            String formatFlag) throws Exception {

        if (filePath == null) {
            throw new KineticToolsException("file path is null");
        }

        if (str == null) {
            throw new KineticToolsException("chassis is null");
        }

        if (formatFlag.equalsIgnoreCase("hwview")) {

            File file = new File(filePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);

            fos.write(str.getBytes());
            fos.flush();
            fos.close();

            return str;
        } else if (formatFlag.equalsIgnoreCase("chassis")) {
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
        } else {
            return null;
        }
    }

    private static List<KineticDevice> parseChassisFromJsonObject(
            JsonArray chassisArray) throws KineticToolsException {
        List<KineticDevice> deviceOfList = new ArrayList<KineticDevice>();

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

        return deviceOfList;
    }
}
