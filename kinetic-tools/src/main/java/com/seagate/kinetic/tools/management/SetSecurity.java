package com.seagate.kinetic.tools.management;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kinetic.admin.ACL;
import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.Domain;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.protobuf.ByteString;
import com.seagate.kinetic.admin.impl.JsonUtil;
import com.seagate.kinetic.proto.Kinetic.Command.Security;
import com.seagate.kinetic.proto.Kinetic.Command.Security.ACL.Permission;

public class SetSecurity extends DeviceLoader{
    private String security;
    private byte[] securityContent;
    private List<ACL> aclList;
    private List<KineticDevice> failed = new ArrayList<KineticDevice>();
    private List<KineticDevice> succeed = new ArrayList<KineticDevice>();

    public SetSecurity(String security, String drivesInputFile)
            throws IOException {
        this.security = security;
        loadSecurity();
        loadDevices(drivesInputFile);
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

    public void setSecurity() throws InterruptedException, KineticException,
            JsonGenerationException, JsonMappingException, IOException {
        CountDownLatch latch = new CountDownLatch(devices.size());
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("Start set security...");

        for (KineticDevice device : devices) {
            pool.execute(new SetSecurityThread(device, aclList, latch));
        }

        // wait all threads finish
        latch.await();
        pool.shutdown();

        int totalDevices = devices.size();
        int failedDevices = failed.size();
        int succeedDevices = succeed.size();

        assert (failedDevices + succeedDevices == totalDevices);

        TimeUnit.SECONDS.sleep(2);
        System.out.flush();
        System.out.println("\nTotal(Succeed/Failed): " + totalDevices + "("
                + succeedDevices + "/" + failedDevices + ")");

        if (succeedDevices > 0) {
            System.out.println("The following devices set security succeed:");
            for (KineticDevice device : succeed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }

        if (failedDevices > 0) {
            System.out.println("The following devices set security failed:");
            for (KineticDevice device : failed) {
                System.out.println(KineticDevice.toJson(device));
            }
        }
    }

    class SetSecurityThread implements Runnable {
        private KineticDevice device = null;
        private KineticAdminClient adminClient = null;
        private AdminClientConfiguration adminClientConfig = null;
        private List<ACL> aclList = null;
        private CountDownLatch latch = null;

        public SetSecurityThread(KineticDevice device, List<ACL> aclList,
                CountDownLatch latch) throws KineticException {
            this.device = device;
            this.aclList = aclList;
            this.latch = latch;
            adminClientConfig = new AdminClientConfiguration();
            adminClientConfig.setHost(device.getInet4().get(0));
            adminClientConfig.setUseSsl(true);
            adminClientConfig.setPort(device.getTlsPort());
            adminClient = KineticAdminClientFactory
                    .createInstance(adminClientConfig);
        }

        @Override
        public void run() {
            try {
                adminClient.setAcl(aclList);
                latch.countDown();

                synchronized (this) {
                    succeed.add(device);
                }

                System.out.println("[Succeed]" + KineticDevice.toJson(device));
            } catch (KineticException e) {
                latch.countDown();

                synchronized (this) {
                    failed.add(device);
                }

                try {
                    System.out.println("[Failed]"
                            + KineticDevice.toJson(device));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    adminClient.close();
                } catch (KineticException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
