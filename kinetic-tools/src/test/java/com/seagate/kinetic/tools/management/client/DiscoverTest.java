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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.seagate.kinetic.tools.management.IntegrationTestCase;
import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.client.RestClientException;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverRequest;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverResponse;

@Test(groups = { "simulator" })
public class DiscoverTest extends IntegrationTestCase {

    private final static String URL = "http://localhost:8080/discover";

    @Test(enabled = false)
    public void test_DiscoverViaScoped() {
        KineticRestClient client = null;
        try {
            client = new KineticRestClient();

            DiscoverRequest request = new DiscoverRequest();
            request.setTimeout(10);
            request.setScoped(true);
            request.setStartIp("127.0.0.1");
            request.setEndIp("127.0.0.100");

            DiscoverResponse response = (DiscoverResponse) client.send(URL,
                    request);

            Assert.assertTrue(response.getMessageType().equals(
                    MessageType.DISCOVER_REPLY));
            Assert.assertTrue(response.getOverallStatus() == 200);
            Assert.assertTrue(response.getDevices().size() > 0);

        } catch (RestClientException e) {
            Assert.fail("rest client throw exception: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("discover throw exception: " + e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
