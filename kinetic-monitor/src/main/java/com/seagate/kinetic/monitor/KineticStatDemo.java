package com.seagate.kinetic.monitor;

import java.util.concurrent.TimeUnit;

public class KineticStatDemo {
	public static void main(String[] args) throws Exception {
		KineticStatModel kineticStatModel = new KineticStatModel();
		KineticStatView kineticStatView = new KineticStatView(
				"Kinetic Statistics");
		KineticOpsOverviewView kineticOpsOverviewView = new KineticOpsOverviewView(
				"Kinetic Drives Ops Snapshot");
		KineticBytesOverviewView kineticBytesOverviewView = new KineticBytesOverviewView(
				"Kinetic Drives Bytes Snapshot");
		KineticStatController kineticStatController = new KineticStatController(
				kineticStatModel, kineticStatView, kineticOpsOverviewView,
				kineticBytesOverviewView);

		kineticStatController.startCollectDataAndUpdateView();

		TimeUnit.SECONDS.sleep(2);
		kineticStatView.render();
		kineticOpsOverviewView.render();
		kineticBytesOverviewView.render();
	}
}
