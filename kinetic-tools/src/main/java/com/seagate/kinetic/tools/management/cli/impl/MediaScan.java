/**
 * Copyright (C) 2014 Seagate Technology.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.tools.management.cli.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kinetic.admin.KineticAdminClient;
import kinetic.client.KineticException;

import com.google.protobuf.ByteString;
import com.seagate.kinetic.client.internal.MessageFactory;
import com.seagate.kinetic.common.lib.KineticMessage;
import com.seagate.kinetic.proto.Kinetic;
import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.RestResponseWithStatus;

public class MediaScan extends AbstractCommand {
    private String startKey;
    private String endKey;
    private String priority;
    private int maxKeys;
    private boolean startKeyInclusive;
    private boolean endKeyInclusive;

    public MediaScan(String startKey, String endKey, boolean startKeyInclusive,
            boolean endKeyInclusive, int maxKeys, String priority,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout, String drivesLogFile) {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesLogFile);
        this.startKey = startKey;
        this.endKey = endKey;
        this.maxKeys = maxKeys;
        this.priority = priority;
        this.startKeyInclusive = startKeyInclusive;
        this.endKeyInclusive = endKeyInclusive;
    }

    private void scanDevices() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new MediaScanThread(device));
        }
        poolExecuteThreadsInGroups(threads);
    }

    private KineticMessage scanDevice(KineticAdminClient adminClient)
            throws KineticException {
        KineticMessage kmreq = MessageFactory.createKineticMessageWithBuilder();
        Kinetic.Command.Builder commandBuilder = (Kinetic.Command.Builder) kmreq
                .getCommand();
        Kinetic.Command.Range.Builder rangeBuilder = commandBuilder
                .getBodyBuilder().getRangeBuilder();
        ByteString startKeyB = ByteString.copyFromUtf8(startKey);
        ByteString endKeyB = ByteString.copyFromUtf8(endKey);

        rangeBuilder.setStartKey(startKeyB);
        rangeBuilder.setEndKey(endKeyB);
        rangeBuilder.setStartKeyInclusive(startKeyInclusive);
        rangeBuilder.setEndKeyInclusive(endKeyInclusive);
        rangeBuilder.setMaxReturned(maxKeys);
        Kinetic.Command.Range range = rangeBuilder.build();

        Kinetic.Command.Priority p = getPriority(priority);

        return adminClient.mediaScan(range, p);
    }

    class MediaScanThread extends AbstractWorkThread {
        public MediaScanThread(KineticDevice device) throws KineticException {
            super(device);
        }

        @Override
        void runTask() throws KineticToolsException {
            try {
                KineticMessage message = scanDevice(adminClient);
                report.setAdditionMessage(device, message);
                report.reportSuccess(device);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        RestResponseWithStatus response = new RestResponseWithStatus();
        List<DeviceStatus> deviceStatusList = new ArrayList<>();
        setRestResponse(response, deviceStatusList);

        response.setDevices(deviceStatusList);

        try {
            String toolHome = System.getProperty("kinetic.tools.out", ".");
            String rootDir = toolHome + File.separator + "out" + File.separator
                    + "mediascan_" + System.currentTimeMillis();

            report.persistReport(response, rootDir,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            scanDevices();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }
}
