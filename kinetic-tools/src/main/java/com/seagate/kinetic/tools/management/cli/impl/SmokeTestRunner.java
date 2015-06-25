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

import org.testng.TestNG;
import org.testng.reporters.SuiteHTMLReporter;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.seagate.kinetic.tools.management.common.KineticToolsException;

public class SmokeTestRunner extends AbstractCommand {
    private String rootDir = ".";

    public SmokeTestRunner(String nodesLogFile) throws IOException {
        super(nodesLogFile);
    }

    private void runSmokeTests() {
        String toolHome = System.getProperty("kinetic.tools.out", ".");
        rootDir = toolHome + File.separator + "SmokeTest-Result"
                + File.separator + "Result-" + System.currentTimeMillis();
        for (KineticDevice device : devices) {
            runSmokeTest(device);
        }
    }

    private void runSmokeTest(KineticDevice device) {
        String host = device.getInet4().get(0);
        String port = device.getPort() + "";
        String sslPort = device.getTlsPort() + "";

        String outputDirectory = rootDir + File.separator + host + "_" + port
                + "_" + sslPort;

        System.setProperty("KINETIC_HOST", host);
        System.setProperty("KINETIC_SSL_PORT", sslPort);
        System.setProperty("KINETIC_PORT", port);

        XmlSuite suite = new XmlSuite();
        suite.setName("DriveSmokeSuite");
        suite.setParallel(XmlSuite.PARALLEL_NONE);

        XmlTest test = new XmlTest(suite);
        test.setName("DriveSmokeTest");
        List<XmlClass> classes = new ArrayList<XmlClass>();
        classes.add(new XmlClass(
                "com.seagate.kinetic.sanityAPI.AdminAPISanityTest"));
        classes.add(new XmlClass(
                "com.seagate.kinetic.sanityAPI.BasicAPISanityTest"));
        test.setXmlClasses(classes);

        List<XmlSuite> suites = new ArrayList<XmlSuite>();
        suites.add(suite);
        TestNG tng = new TestNG();
        tng.addListener(new SuiteHTMLReporter());
        tng.setXmlSuites(suites);
        tng.setOutputDirectory(outputDirectory);
        tng.run();
    }

    @Override
    public void execute() throws KineticToolsException {
        try {
            runSmokeTests();
        } catch (Exception e) {
            throw new KineticToolsException(e);
        }
    }

    @Override
    public void done() throws KineticToolsException {
        System.out.println("All tests result has been stored at " + rootDir);
    }
}
