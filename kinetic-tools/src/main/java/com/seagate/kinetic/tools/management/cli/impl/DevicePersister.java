package com.seagate.kinetic.tools.management.cli.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

abstract class DevicePersister {
    protected List<KineticDevice> devices;

    protected String persistToFile(List<KineticDevice> devicesList,
            String persistFilePath) throws JsonGenerationException,
            JsonMappingException, IOException {
        assert (devicesList != null);
        assert (persistFilePath != null);

        FileOutputStream fos = new FileOutputStream(persistFilePath);
        StringBuffer sb = new StringBuffer();
        for (KineticDevice device : devicesList) {
            sb.append(KineticDevice.toJson(device));
            sb.append("\n");
        }

        fos.write(sb.toString().getBytes("UTF-8"));
        fos.flush();
        fos.close();

        return sb.toString();
    }
}
