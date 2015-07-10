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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.RestResponseWithStatus;

public class FirmwareDownload extends AbstractCommand {
    private static final int CHUNK_SIZE = 1024;
    private String firmware;
    private byte[] firmwareContent;

    public FirmwareDownload(String firmware, String drivesLogFile,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesLogFile);
        this.firmware = firmware;
    }

    public FirmwareDownload(String firmware, List<DeviceId> deviceIds,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, deviceIds);
        this.firmware = firmware;
    }

    private void loadFirmware() throws IOException {
        InputStream is = new FileInputStream(firmware);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[CHUNK_SIZE];
        int n;
        while ((n = is.read(b)) != -1) {
            out.write(b, 0, n);
        }
        is.close();
        firmwareContent = out.toByteArray();
    }

    private void updateFirmware() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new FirmwareDownloadThread(firmwareContent, device));
        }
        poolExecuteThreadsInGroups(threads);
    }

    class FirmwareDownloadThread extends AbstractWorkThread {
        private byte[] firmwareContent = null;

        public FirmwareDownloadThread(byte[] firmwareContent,
                KineticDevice device) throws KineticException {
            super(device);
            this.firmwareContent = firmwareContent;
        }

        @Override
        void runTask() throws KineticToolsException {
            try {
                adminClient.firmwareDownload(firmwareContent);
                report.reportSuccess(device);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void init() throws KineticToolsException {
        super.init();

        try {
            loadFirmware();
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            updateFirmware();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        RestResponseWithStatus response = new RestResponseWithStatus();
        try {
            String toolHome = System.getProperty("kinetic.tools.out", ".");
            String rootDir = toolHome + File.separator + "out" + File.separator
                    + "firmwaredownload_" + System.currentTimeMillis();

            report.persistReport(response, rootDir,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }
}
