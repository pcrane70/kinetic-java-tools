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

public class NodeStatItem {
	private double totalPutTimes;
	private double totalPutBytes;
	private double totalGetTimes;
	private double totalGetBytes;
	private double totalDeleteTimes;
	private double totalDeleteBytes;
	private double recordTimeInMilliSec;

	public NodeStatItem(double totalPutTimes, double totalPutBytes,
			double totalGetTimes, double totalGetBytes,
			double totalDeleteTimes, double totalDeleteBytes) {
		this.totalPutTimes = totalPutTimes;
		this.totalPutBytes = totalPutBytes;
		this.totalGetTimes = totalGetTimes;
		this.totalGetBytes = totalGetBytes;
		this.totalDeleteTimes = totalDeleteTimes;
		this.totalDeleteBytes = totalDeleteBytes;

		recordTimeInMilliSec = System.currentTimeMillis();
	}

	public NodeStatItem(double totalPutTimes, double totalPutBytes,
			double totalGetTimes, double totalGetBytes,
			double totalDeleteTimes, double totalDeleteBytes,
			double recordTimeInMilliSec) {
		this.totalPutTimes = totalPutTimes;
		this.totalPutBytes = totalPutBytes;
		this.totalGetTimes = totalGetTimes;
		this.totalGetBytes = totalGetBytes;
		this.totalDeleteTimes = totalDeleteTimes;
		this.totalDeleteBytes = totalDeleteBytes;
		this.recordTimeInMilliSec = recordTimeInMilliSec;
	}

	public double getTotalPutTimes() {
		return totalPutTimes;
	}

	public void setTotalPutTimes(double totalPutTimes) {
		this.totalPutTimes = totalPutTimes;
	}

	public double getTotalPutBytes() {
		return totalPutBytes;
	}

	public void setTotalPutBytes(double totalPutBytes) {
		this.totalPutBytes = totalPutBytes;
	}

	public double getTotalGetTimes() {
		return totalGetTimes;
	}

	public void setTotalGetTimes(double totalGetTimes) {
		this.totalGetTimes = totalGetTimes;
	}

	public double getTotalGetBytes() {
		return totalGetBytes;
	}

	public void setTotalGetBytes(double totalGetBytes) {
		this.totalGetBytes = totalGetBytes;
	}

	public double getTotalDeleteTimes() {
		return totalDeleteTimes;
	}

	public void setTotalDeleteTimes(double totalDeleteTimes) {
		this.totalDeleteTimes = totalDeleteTimes;
	}

	public double getTotalDeleteBytes() {
		return totalDeleteBytes;
	}

	public void setTotalDeleteBytes(double totalDeleteBytes) {
		this.totalDeleteBytes = totalDeleteBytes;
	}

	public double getRecordTimeInMilliSec() {
		return recordTimeInMilliSec;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("totalPutTimes: " + totalPutTimes + ", ");
		sb.append("totalPutBytes: " + totalPutBytes + ", ");
		sb.append("totalGetTimes: " + totalGetTimes + ", ");
		sb.append("totalGetBytes: " + totalGetBytes + ", ");
		sb.append("totalDeleteTimes: " + totalDeleteTimes + ", ");
		sb.append("totalDeleteBytes: " + totalDeleteBytes + ", ");
		sb.append("recordTimeInMilliSec: " + recordTimeInMilliSec);
		return sb.toString();
	}

	@Override
	public NodeStatItem clone() {
		return new NodeStatItem(totalPutTimes, totalPutBytes, totalGetTimes,
				totalGetBytes, totalDeleteTimes, totalDeleteBytes,
				recordTimeInMilliSec);
	}
}