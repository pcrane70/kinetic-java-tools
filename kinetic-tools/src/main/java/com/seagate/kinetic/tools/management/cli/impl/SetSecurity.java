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

import kinetic.admin.ACL;
import kinetic.admin.Domain;
import kinetic.client.KineticException;

import com.google.protobuf.ByteString;
import com.seagate.kinetic.admin.impl.JsonUtil;
import com.seagate.kinetic.proto.Kinetic.Command.Security;
import com.seagate.kinetic.proto.Kinetic.Command.Security.ACL.Permission;
import com.seagate.kinetic.tools.management.common.KineticToolsException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.setsecurity.SetSecurityResponse;

public class SetSecurity extends AbstractCommand {
    private String security;
    private byte[] securityContent;
    private List<ACL> aclList;

    public SetSecurity(String security, String drivesInputFile, boolean useSsl,
            long clusterVersion, long identity, String key, long requestTimeout)
            throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesInputFile);
        this.security = security;
        loadSecurity();
    }

    public SetSecurity(List<ACL> acls, List<DeviceId> deviceIds,
            boolean useSsl, long clusterVersion, long identity, String key,
            long requestTimeout) throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout, deviceIds);
        this.aclList = acls;
    }

    public SetSecurity(List<ACL> acls, String drivesInputFile, boolean useSsl,
            long clusterVersion, long identity, String key, long requestTimeout)
            throws IOException {
        super(useSsl, clusterVersion, identity, key, requestTimeout,
                drivesInputFile);
        this.aclList = acls;
    }

    private void loadSecurity() throws IOException {
        InputStream is = new FileInputStream(security);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = is.read(b)) != -1) {
            out.write(b, 0, n);
        }
        is.close();
        securityContent = out.toByteArray();

        Security security = JsonUtil.parseSecurity(ByteString.copyFrom(
                securityContent).toStringUtf8());

        ACL myAcl = null;
        Domain myDomain = null;
        aclList = new ArrayList<ACL>();
        List<Domain> myDomainList = null;
        for (com.seagate.kinetic.proto.Kinetic.Command.Security.ACL acl : security
                .getAclList()) {
            myAcl = new ACL();

            myAcl.setUserId(acl.getIdentity());
            myAcl.setKey(acl.getKey().toStringUtf8());

            myDomainList = new ArrayList<Domain>();
            List<kinetic.admin.Role> roleList = null;
            for (com.seagate.kinetic.proto.Kinetic.Command.Security.ACL.Scope domain : acl
                    .getScopeList()) {
                myDomain = new Domain();
                roleList = new ArrayList<kinetic.admin.Role>();
                myDomain.setOffset(domain.getOffset());
                myDomain.setValue(domain.getValue().toStringUtf8());
                for (Permission role : domain.getPermissionList()) {
                    roleList.add(kinetic.admin.Role.valueOf(role.toString()));
                }
                myDomain.setRoles(roleList);
                myDomainList.add(myDomain);
            }
            myAcl.setDomains(myDomainList);

            aclList.add(myAcl);
        }
    }

    private void setSecurity() throws Exception {
        if (null == devices || devices.isEmpty()) {
            throw new Exception("Drives get from input file are null or empty.");
        }

        List<AbstractWorkThread> threads = new ArrayList<AbstractWorkThread>();
        for (KineticDevice device : devices) {
            threads.add(new SetSecurityThread(device, aclList));
        }
        poolExecuteThreadsInGroups(threads);
    }

    class SetSecurityThread extends AbstractWorkThread {
        private List<ACL> aclList = null;

        public SetSecurityThread(KineticDevice device, List<ACL> aclList)
                throws KineticException {
            super(device);
            this.aclList = aclList;
        }

        @Override
        void runTask() throws KineticToolsException {
            try {
                adminClient.setAcl(aclList);
                report.reportSuccess(device);
            } catch (KineticException e) {
                throw new KineticToolsException(e);
            }
        }
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            setSecurity();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        super.done();
        SetSecurityResponse response = new SetSecurityResponse();
        try {
            String toolHome = System.getProperty("kinetic.tools.out", ".");
            String rootDir = toolHome + File.separator + "out" + File.separator
                    + "setsecurity_" + System.currentTimeMillis();

            report.persistReport(response, rootDir,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            throw new KineticToolsException(e);
        }
    }
}
