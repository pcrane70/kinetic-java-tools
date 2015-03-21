package com.seagate.kinetic.tools.management.cli.impl;

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

import kinetic.admin.AdminClientConfiguration;
import kinetic.admin.KineticAdminClient;
import kinetic.admin.KineticAdminClientFactory;
import kinetic.client.KineticException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

public class FirmwareDownloader extends DeviceLoader{
	private String firmware;
	private byte[] firmwareContent;
	private List<KineticDevice> failed = new ArrayList<KineticDevice>();
	private List<KineticDevice> succeed = new ArrayList<KineticDevice>();

	public FirmwareDownloader(String firmware, String nodesLogFile)
			throws IOException {
		this.firmware = firmware;
		loadFirmware();
		loadDevices(nodesLogFile);
	}

	private void loadFirmware() throws IOException {
		InputStream is = new FileInputStream(firmware);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = is.read(b)) != -1) {
			out.write(b, 0, n);
		}
		is.close();
		firmwareContent = out.toByteArray();
	}

	public void updateFirmware() throws InterruptedException, KineticException,
			JsonGenerationException, JsonMappingException, IOException {
		CountDownLatch latch = new CountDownLatch(devices.size());
		ExecutorService pool = Executors.newCachedThreadPool();
		
		System.out.println("Start download firmware......");

		for (KineticDevice device : devices) {
			pool.execute(new FirmwareDownloadThread(device, firmwareContent,
					latch));
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
			System.out.println("Below devices downloaded firmware succeed:");
			for (KineticDevice device : succeed) {
				System.out.println(KineticDevice.toJson(device));
			}
		}

		if (failedDevices > 0) {
			System.out.println("The following devices downloaded firmware failed:");
			for (KineticDevice device : failed) {
				System.out.println(KineticDevice.toJson(device));
			}
		}
	}

	class FirmwareDownloadThread implements Runnable {
		private KineticDevice device = null;
		private KineticAdminClient adminClient = null;
		private AdminClientConfiguration adminClientConfig = null;
		private byte[] firmwareContent = null;
		private CountDownLatch latch = null;

		public FirmwareDownloadThread(KineticDevice device,
				byte[] firmwareContent, CountDownLatch latch)
				throws KineticException {
			this.device = device;
			this.firmwareContent = firmwareContent;
			this.latch = latch;
			adminClientConfig = new AdminClientConfiguration();
			adminClientConfig.setHost(device.getInet4().get(0));
			adminClientConfig.setUseSsl(false);
			adminClientConfig.setPort(device.getPort());
			adminClient = KineticAdminClientFactory
					.createInstance(adminClientConfig);
		}

		@Override
		public void run() {
			try {
				adminClient.firmwareDownload(firmwareContent);
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
				    System.out.println(e.getMessage());
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
