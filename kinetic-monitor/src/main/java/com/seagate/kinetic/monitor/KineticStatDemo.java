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
package com.seagate.kinetic.monitor;

import java.util.concurrent.TimeUnit;

import com.seagate.kinetic.monitor.controller.KineticStatController;
import com.seagate.kinetic.monitor.model.KineticStatModel;
import com.seagate.kinetic.monitor.view.KineticBytesOverviewView;
import com.seagate.kinetic.monitor.view.KineticOpsOverviewView;
import com.seagate.kinetic.monitor.view.KineticSpecifiedNodeView;

public class KineticStatDemo {
	public static void main(String[] args) throws Exception {
		KineticStatModel kineticStatModel = new KineticStatModel();
		KineticSpecifiedNodeView kineticStatView = new KineticSpecifiedNodeView(
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
