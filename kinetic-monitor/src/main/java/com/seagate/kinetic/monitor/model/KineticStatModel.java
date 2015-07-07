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
package com.seagate.kinetic.monitor.model;

import java.util.HashMap;
import java.util.Map;

public class KineticStatModel {
	private Map<String, Map<String, NodeStatItem>> nodesStat;
	private final static String CURRENT = "current";
	private final static String PREVIOUS = "previous";

	public KineticStatModel() {
		nodesStat = new HashMap<String, Map<String, NodeStatItem>>();
	}

	public synchronized int getNodeCount() {
		return nodesStat.keySet().size();
	}

	public synchronized void updateNodeStat(String node, double totalPutTimes,
			double totalPutBytes, double totalGetTimes, double totalGetBytes,
			double totalDeleteTimes, double totalDeleteBytes) {
		Map<String, NodeStatItem> nodeStats= null;
		if ((nodeStats = nodesStat.get(node)) == null) {
			nodeStats = new HashMap<String, NodeStatItem>();
			nodeStats.put(CURRENT, new NodeStatItem(totalPutTimes,
					totalPutBytes, totalGetTimes, totalGetBytes,
					totalDeleteTimes, totalDeleteBytes));
			nodeStats.put(PREVIOUS, new NodeStatItem(0, 0, 0, 0, 0, 0, 0));
			nodesStat.put(node, nodeStats);
		} else {
			nodeStats.put(PREVIOUS, nodeStats.get(CURRENT));
			nodeStats.put(CURRENT, new NodeStatItem(totalPutTimes,
					totalPutBytes, totalGetTimes, totalGetBytes,
					totalDeleteTimes, totalDeleteBytes));
		}
	}

	public synchronized void updateNodeStat(String node, NodeStatItem nodeStat) {
		updateNodeStat(node, nodeStat.getTotalPutTimes(),
				nodeStat.getTotalPutBytes(), nodeStat.getTotalGetTimes(),
				nodeStat.getTotalGetBytes(), nodeStat.getTotalDeleteTimes(),
				nodeStat.getTotalDeleteBytes());
	}

	public synchronized NodeStatItem getAvgSystemStat() {
		int totalNodes = nodesStat.size();
		if (totalNodes == 0) {
			return new NodeStatItem(0, 0, 0, 0, 0, 0);
		}

		double putTimesInSec = 0;
		double putBytesInSec = 0;
		double getTimesInSec = 0;
		double getBytesInSec = 0;
		double deleteTimesInSec = 0;
		double deleteBytesInSec = 0;

		NodeStatItem nodeStat = null;
		for (String node : nodesStat.keySet()) {
			nodeStat = getAvgNodeStat(node);
			putTimesInSec += nodeStat.getTotalPutTimes();
			putBytesInSec += nodeStat.getTotalPutBytes();
			getTimesInSec += nodeStat.getTotalGetTimes();
			getBytesInSec += nodeStat.getTotalGetBytes();
			deleteTimesInSec += nodeStat.getTotalDeleteTimes();
			deleteBytesInSec += nodeStat.getTotalDeleteBytes();
		}

		return new NodeStatItem(putTimesInSec, putBytesInSec, getTimesInSec,
				getBytesInSec, deleteTimesInSec, deleteBytesInSec);
	}

	public synchronized NodeStatItem getCurrentNodeStat(String node) {
		Map<String, NodeStatItem> nodeStats = null;
		if ((nodeStats = nodesStat.get(node)) == null) {
			return new NodeStatItem(0, 0, 0, 0, 0, 0, 0);
		}

		NodeStatItem current = nodeStats.get(CURRENT);
		return current;
	}

	public synchronized NodeStatItem getAvgNodeStat(String node) {
		Map<String, NodeStatItem> nodeStats = null;
		if ((nodeStats = nodesStat.get(node)) == null) {
			return new NodeStatItem(0, 0, 0, 0, 0, 0, 0);
		}

		NodeStatItem current = nodeStats.get(CURRENT);
		NodeStatItem previous = nodeStats.get(PREVIOUS);

		if (previous.getRecordTimeInMilliSec() == 0) {
			return new NodeStatItem(0, 0, 0, 0, 0, 0, 0);
		}

		double intervalInSec = (current.getRecordTimeInMilliSec() - previous
				.getRecordTimeInMilliSec()) / 1000;
		double putTimesInSec = (current.getTotalPutTimes() - previous
				.getTotalPutTimes()) / intervalInSec;
		double putBytesInSec = (current.getTotalPutBytes() - previous
				.getTotalPutBytes()) / intervalInSec;
		double getTimesInSec = (current.getTotalGetTimes() - previous
				.getTotalGetTimes()) / intervalInSec;
		double getBytesInSec = (current.getTotalGetBytes() - previous
				.getTotalGetBytes()) / intervalInSec;
		double deleteTimesInSec = (current.getTotalDeleteTimes() - previous
				.getTotalDeleteTimes()) / intervalInSec;
		double deleteBytesInSec = (current.getTotalDeleteBytes() - previous
				.getTotalDeleteBytes()) / intervalInSec;

		return new NodeStatItem(putTimesInSec, putBytesInSec, getTimesInSec,
				getBytesInSec, deleteTimesInSec, deleteBytesInSec);
	}
}
