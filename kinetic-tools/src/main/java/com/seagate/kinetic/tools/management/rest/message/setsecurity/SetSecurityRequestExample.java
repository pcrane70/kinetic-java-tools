package com.seagate.kinetic.tools.management.rest.message.setsecurity;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.ACL;
import kinetic.admin.Domain;

import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

public class SetSecurityRequestExample {

    public static void main(String[] args) {

        SetSecurityRequest req = new SetSecurityRequest();

        List<DeviceId> devices = new ArrayList<DeviceId>();

        DeviceId deviceId = new DeviceId();

        deviceId.setWwn("1234");
        String[] ips = { "127.0.0.1" };
        deviceId.setIps(ips);

        devices.add(deviceId);

        req.setDevices(devices);

        List<ACL> acls = new ArrayList<ACL>();
        
        // acl
        ACL acl = new ACL();
        acl.setUserId(1);
        acl.setKey("123");
        acl.setAlgorithm("SHA1");
       
        List<Domain> domains = new ArrayList<Domain>();

        // domain
        Domain domain = new Domain();
        List<kinetic.admin.Role> roles = new ArrayList<kinetic.admin.Role>();
        roles.add(kinetic.admin.Role.DELETE);
        roles.add(kinetic.admin.Role.WRITE);
        
        domains.add(domain);

        // set roles
        domain.setRoles(roles);
        
        // set domain
        acl.setDomains(domains);

        // add acl
        acls.add(acl);
        
        req.setAcl(acls);
        
        String request = req.toJson();

        System.out.println(request);

        SetSecurityRequest req2 = (SetSecurityRequest) MessageUtil.fromJson(
                request,
 SetSecurityRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

        System.out.println("toString: " + req2.toString());

    }

}
