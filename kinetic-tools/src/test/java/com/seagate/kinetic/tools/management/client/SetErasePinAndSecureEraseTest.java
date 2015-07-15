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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import kinetic.client.ClientConfiguration;
import kinetic.client.Entry;
import kinetic.client.KineticClient;
import kinetic.client.KineticClientFactory;
import kinetic.client.KineticException;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.seagate.kinetic.tools.management.IntegrationTestCase;
import com.seagate.kinetic.tools.management.KineticTestHelper;
import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.client.RestClientException;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.SecureEraseRequest;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetErasePinRequest;

@Test(groups = { "simulator" })
public class SetErasePinAndSecureEraseTest extends IntegrationTestCase {

    private final static String URL_SET_ERASE_PIN = "http://localhost:8080/seterasepin";
    private final static String URL_INSTATNT_ERASE = "http://localhost:8080/secureerase";
    private final static String OLD_PIN = "";
    private final static String NEW_PIN = "123";
    private final static String PREIX_KEY = "key";
    private final static String PREIX_VALUE = "value";
    private final static int PREPARE_KEY_NUMBER = 100;
    private static KineticClient client = null;

    @BeforeMethod
    public void beforeMethod() throws KineticException {
        ClientConfiguration config = new ClientConfiguration();
        client = KineticClientFactory.createInstance(config);
    }

    @AfterMethod
    public void afterMethod() throws KineticException {
        if (null != client) {
            client.close();
        }
    }

    @Test
    public void test_SetErasePinAndInstantEraseViaDiscoId() {
        KineticRestClient client = null;
        try {
            prePutData();

            client = new KineticRestClient();

            SetErasePinRequest request = new SetErasePinRequest();
            request.setDiscoId(KineticTestHelper.FILE_NAME);
            request.setOldPin(OLD_PIN);
            request.setNewPin(NEW_PIN);

            RestResponse response = client.send(URL_SET_ERASE_PIN, request);

            Assert.assertTrue(response.getMessageType().equals(
                    MessageType.SET_ERASEPIN_REPLY));
            Assert.assertTrue(response.getOverallStatus() == 200);

            SecureEraseRequest instantEraseReq = new SecureEraseRequest();
            instantEraseReq.setDiscoId(KineticTestHelper.FILE_NAME);
            instantEraseReq.setPin(NEW_PIN);

            RestResponse instantEraseResp = client.send(URL_INSTATNT_ERASE,
                    instantEraseReq);

            Assert.assertTrue(instantEraseResp.getMessageType().equals(
                    MessageType.SECURE_ERASE_REPLY));
            Assert.assertTrue(instantEraseResp.getOverallStatus() == 200);

        } catch (RestClientException e) {
            Assert.fail("rest client throw exception: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("set erase pin or instant erase throw exception: "
                    + e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }

        checkData();
    }

    @Test
    public void test_SetErasePinAndInstantEraseViaDeviceList() {
        KineticRestClient client = null;

        try {
            prePutData();

            client = new KineticRestClient();

            SetErasePinRequest request = new SetErasePinRequest();

            List<DeviceId> deviceIdOfList = new ArrayList<DeviceId>();
            DeviceId deviceId = new DeviceId();
            String[] ips = new String[] { "127.0.0.1" };
            deviceId.setIps(ips);
            deviceId.setPort(KineticTestHelper.PORT);
            deviceId.setTlsPort(KineticTestHelper.SSL_PORT);

            deviceIdOfList.add(deviceId);

            request.setDevices(deviceIdOfList);

            request.setOldPin(OLD_PIN);
            request.setNewPin(NEW_PIN);

            RestResponse response = client.send(URL_SET_ERASE_PIN, request);

            Assert.assertTrue(response.getMessageType().equals(
                    MessageType.SET_ERASEPIN_REPLY));
            Assert.assertTrue(response.getOverallStatus() == 200);

            SecureEraseRequest instantEraseReq = new SecureEraseRequest();
            instantEraseReq.setDiscoId(KineticTestHelper.FILE_NAME);
            instantEraseReq.setPin(NEW_PIN);

            RestResponse instantEraseResp = client.send(URL_INSTATNT_ERASE,
                    instantEraseReq);

            Assert.assertTrue(instantEraseResp.getMessageType().equals(
                    MessageType.SECURE_ERASE_REPLY));
            Assert.assertTrue(instantEraseResp.getOverallStatus() == 200);

        } catch (RestClientException e) {
            Assert.fail("rest client throw exception: " + e.getMessage());
        } catch (Exception e) {
            Assert.fail("set erase pin or instant erase throw exception: "
                    + e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }

        checkData();
    }

    /**
     * @throws KineticException
     */
    private void prePutData() {
        for (int i = 0; i < PREPARE_KEY_NUMBER; i++) {
            Entry entry = new Entry();
            byte[] key = (PREIX_KEY + i).getBytes(Charset.forName("UTF-8"));
            byte[] value = (PREIX_VALUE + i).getBytes(Charset.forName("UTF-8"));
            entry.setKey(key);
            entry.setValue(value);
            try {
                client.putForced(entry);
            } catch (KineticException e) {
                Assert.fail("put prepare data throw exception: "
                        + e.getMessage());
            }
        }
    }

    private void checkData() {
        for (int i = 0; i < PREPARE_KEY_NUMBER; i++) {
            byte[] key = (PREIX_KEY + i).getBytes(Charset.forName("UTF-8"));
            try {
                Entry entry = client.get(key);
                Assert.assertNull(entry);

            } catch (KineticException e) {
                Assert.fail("check data clean or not throw exception: "
                        + e.getMessage());
            }
        }
    }
}
