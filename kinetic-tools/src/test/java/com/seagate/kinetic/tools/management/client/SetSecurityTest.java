/**
 * 
 * Copyright (C) 2014 Seagate Technology.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.seagate.kinetic.tools.management.client;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.ACL;
import kinetic.admin.Domain;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.seagate.kinetic.tools.management.IntegrationTestCase;
import com.seagate.kinetic.tools.management.KineticTestHelper;
import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.client.RestClientException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.setsecurity.SetSecurityRequest;

@Test(groups = { "simulator" })
public class SetSecurityTest extends IntegrationTestCase {

    private final static String URL = "http://localhost:8080/setsecurity";

    @Test
    public void test_SetSecurityViaDiscoId() {
        KineticRestClient client = null;
        try {
            client = new KineticRestClient();

            SetSecurityRequest request = new SetSecurityRequest();
            request.setDiscoId(KineticTestHelper.FILE_NAME);
            request.setAcl(generateACLs());

            RestResponse response = client.send(URL, request);

            Assert.assertTrue(response.getMessageType().equals(
                    MessageType.SET_SECURITY_REPLY));
            Assert.assertTrue(response.getOverallStatus() == 200);

        } catch (RestClientException e) {
            Assert.fail("rest client throw exception: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("set security throw exception: " + e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test
    public void test_SetsecurityViaDeviceList() {
        KineticRestClient client = null;

        try {
            client = new KineticRestClient();

            SetSecurityRequest request = new SetSecurityRequest();

            List<DeviceId> deviceIdOfList = new ArrayList<DeviceId>();
            DeviceId deviceId = new DeviceId();
            String[] ips = new String[] { "127.0.0.1" };
            deviceId.setIps(ips);
            deviceId.setPort(KineticTestHelper.PORT);
            deviceId.setTlsPort(KineticTestHelper.SSL_PORT);

            deviceIdOfList.add(deviceId);

            request.setDevices(deviceIdOfList);
            request.setAcl(generateACLs());

            // send request
            RestResponse response = client.send(URL, request);

            Assert.assertTrue(response.getMessageType().equals(
                    MessageType.SET_SECURITY_REPLY));
            Assert.assertTrue(response.getOverallStatus() == 200);

        } catch (RestClientException e) {
            Assert.fail("rest client throw exception: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("set security throw exception: " + e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private List<ACL> generateACLs() {
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
        roles.add(kinetic.admin.Role.GETLOG);
        roles.add(kinetic.admin.Role.READ);
        roles.add(kinetic.admin.Role.SETUP);
        roles.add(kinetic.admin.Role.RANGE);
        roles.add(kinetic.admin.Role.P2POP);

        domains.add(domain);

        // set roles
        domain.setRoles(roles);

        // set domain
        acl.setDomains(domains);

        // add acl
        acls.add(acl);

        return acls;
    }
}
