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
package com.seagate.kinetic.tools.management.rest.client.setsecurity;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.ACL;
import kinetic.admin.Domain;

import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.setsecurity.SetSecurityRequest;

public class SetSecurityClientExample {

    public static void main(String[] args) throws Exception {

        KineticRestClient client = new KineticRestClient();

        // default url
        String url = "http://localhost:8080/setsecurity";

        // override with arg[0]
        if (args.length > 0) {
            url = args[0];
        }

        SetSecurityRequest request = new SetSecurityRequest();

        // List<DeviceId> devices = new ArrayList<DeviceId>();
        //
        // DeviceId deviceId = new DeviceId();
        //
        // deviceId.setWwn("1234");
        // String[] ips = { "127.0.0.1" };
        // deviceId.setIps(ips);
        //
        // devices.add(deviceId);

        // request.setDevices(devices);

        request.setDiscoId("321");

        List<ACL> acls = new ArrayList<ACL>();

        // acl
        ACL acl = new ACL();
        acl.setUserId(1);
        acl.setKey("asdfasdf");
        acl.setAlgorithm("HmacSHA1");

        List<Domain> domains = new ArrayList<Domain>();

        // domain
        Domain domain = new Domain();
        List<kinetic.admin.Role> roles = new ArrayList<kinetic.admin.Role>();
        roles.add(kinetic.admin.Role.DELETE);
        roles.add(kinetic.admin.Role.WRITE);
        roles.add(kinetic.admin.Role.SECURITY);

        domains.add(domain);

        // set roles
        domain.setRoles(roles);

        // set domain
        acl.setDomains(domains);

        // add acl
        acls.add(acl);

        request.setAcl(acls);

        // send request
        RestResponse response = client.send(url, request);

        // print response
        System.out.println(response.toJson());

        // close client
        client.close();
    }


}
