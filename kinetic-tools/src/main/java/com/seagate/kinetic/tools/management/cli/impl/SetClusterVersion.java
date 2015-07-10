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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import kinetic.client.KineticException;

import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.setclversion.SetClusterVersionResponse;

public class SetClusterVersion extends AbstractCommand {
    private long newClusterVersion;

    public SetClusterVersion(String clusterVersionInString,
            String drivesInputFile, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesInputFile);
        this.newClusterVersion = Long.parseLong(clusterVersionInString);
    }

    public SetClusterVersion(String clusterVersionInString,
            List<DeviceId> deviceIds, boolean useSsl, long clusterVersion,
            long identity, String key, long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, deviceIds);
        this.newClusterVersion = Long.parseLong(clusterVersionInString);
    }

    private void setClusterVersion() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new setClusterVersionThread(device, newClusterVersion));
        }
        poolExecuteThreadsInGroups(threads);
    }

    class setClusterVersionThread extends AbstractWorkThread {
        private long newClusterVersion = 0;

        public setClusterVersionThread(KineticDevice device,
                long newClusterVersion) throws KineticException {
            super(device);
            this.newClusterVersion = newClusterVersion;
        }

        @Override
        void runTask() throws KineticToolsException {
            try {
                adminClient.setClusterVersion(newClusterVersion);
                report.reportSuccess(device);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            setClusterVersion();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        SetClusterVersionResponse response = new SetClusterVersionResponse();
        try {
            String toolHome = System.getProperty("kinetic.tools.out", ".");
            String rootDir = toolHome + File.separator + "out" + File.separator
                    + "setclusterversion_" + System.currentTimeMillis();

            report.persistReport(response, rootDir,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }
}
