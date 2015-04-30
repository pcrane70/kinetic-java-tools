package com.seagate.kinetic.monitor;

import java.util.concurrent.TimeUnit;

public class KineticStatDemo {
	public static void main(String[] args) throws Exception {
		KineticStatModel kineticStatModel = new KineticStatModel();
		KineticStatView kineticStatView = new KineticStatView(
				"Kinetic Statistics");
		KineticOverviewView kineticOverviewView = new KineticOverviewView(
				"Kinetic Drives Snapshot");
		KineticStatController kineticStatController = new KineticStatController(
				kineticStatModel, kineticStatView, kineticOverviewView);

		kineticStatController.startCollectDataAndUpdateView();

		TimeUnit.SECONDS.sleep(2);
		kineticStatView.render();
		kineticOverviewView.render();
	}
}
