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
package com.seagate.kinetic.tools.management;

import java.io.IOException;

import kinetic.client.KineticException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Kinetic client integration test case.
 * <p>
 * The methods are used before and after every kinetic client test case.
 * <p>
 *
 *
 */
public class IntegrationTestCase {
    private AbstractIntegrationTestTarget testTarget;
    private RestServiceTarget restServiceTarget;

    /**
     * Initialize a test server and start a rest service.
     * <p>
     * 
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void startTestServer() throws Exception {
        restServiceTarget = new RestServiceTarget();
        testTarget = IntegrationTestTargetFactory.createTestTarget(true);
        KineticTestHelper.generateDeviceFile();
    }

    /**
     * Stop a test server and a Kinetic client.
     * <p>
     *
     * @throws KineticException
     *             if any internal error occurred.
     * @throws IOException
     *             if any IO error occurred.
     */
    @AfterClass(alwaysRun = true)
    public void stopTestServer() throws Exception {
        testTarget.shutdown();
        KineticTestHelper.removeDefaultDeviceFile();
        restServiceTarget.close();
    }

    /**
     * Restart the server.
     * <p>
     */
    protected void restartServer() throws Exception {
        testTarget.shutdown();
        testTarget = IntegrationTestTargetFactory.createTestTarget(false);
    }
}
