package com.seagate.kinetic.tools.external.swift.ring;

import java.util.List;

public class Opartition {

    private int partitionId = 0;

    private List<Integer> driveIds = null;

    public Opartition() {
        // TODO Auto-generated constructor stub
    }

    public void setPartitionId(int pid) {
        this.partitionId = pid;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public void setDriveIds(List<Integer> ids) {
        this.driveIds = ids;
    }

    public List<Integer> getDriveIds() {
        return this.driveIds;
    }

}
