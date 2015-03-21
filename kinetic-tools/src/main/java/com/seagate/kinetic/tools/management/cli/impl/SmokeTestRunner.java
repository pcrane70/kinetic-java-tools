package com.seagate.kinetic.tools.management.cli.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;
import org.testng.reporters.SuiteHTMLReporter;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class SmokeTestRunner extends DeviceLoader{
	public SmokeTestRunner(String nodesLogFile) throws IOException {
		loadDevices(nodesLogFile);
	}

	public void runSmokeTests() {
		System.out.println("Start run smoke tests......");
		for (KineticDevice device : devices) {
			runSmokeTest(device);
		}
	}

	private void runSmokeTest(KineticDevice device) {
		System.setProperty("KINETIC_HOST", device.getInet4().get(0));
		System.setProperty("KINETIC_SSL_PORT", device.getTlsPort() + "");
		System.setProperty("KINETIC_PORT", device.getPort() + "");
		
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
		tng.run();
	}
}
