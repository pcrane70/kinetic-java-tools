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
package com.seagate.kinetic.tools.external.swift.ring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ORingResponseExample {

    public ORingResponseExample() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {

        OringResponse ringResponse = new OringResponse();

        SortedMap<Integer, List<Integer>> pmap = new TreeMap<Integer, List<Integer>>();

        int partitions = 1000;
        int nDrives = 100;

        int[] driveIds = new int[nDrives];
        for (int i = 0; i < nDrives; i++) {
            driveIds[i] = i;
        }

        int driveIndex = 0;

        // 100 drives
        for (int i = 0; i < partitions; i++) {

            // get drive ids
            List<Integer> ids = new ArrayList<Integer>();

            for (int k = 0; k < 3; k++) {

                if (driveIndex == nDrives) {
                    driveIndex = 0;
                }

                ids.add(Integer.valueOf(driveIds[driveIndex]));

                driveIndex++;
            }

            pmap.put(Integer.valueOf(i), ids);
        }

        ArrayList<Opartition> oring = new ArrayList<Opartition>();

        Iterator<Integer> it = pmap.keySet().iterator();
        while (it.hasNext()) {

            Integer key = it.next();
            List<Integer> ids = pmap.get(key);

            Opartition p = new Opartition();
            p.setPartitionId(key.intValue());
            p.setDriveIds(ids);

            oring.add(p);
        }

        ringResponse.setRing(oring);

        System.out.println(ringResponse.toJson());
    }

}
