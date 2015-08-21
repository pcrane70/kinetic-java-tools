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
package com.seagate.kinetic.tools.external.swift;

import java.util.SortedMap;
import java.util.TreeMap;

import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;


/**
 * Kinetic swift sample ring
 * 
 * @author chiaming
 *
 */
public class SampleRing implements ExternalCommandService {

    public SampleRing() {
        ;
    }

    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

        System.out.println("** received request: " + request.toJson());

        ObjectRingResponse ringResponse = new ObjectRingResponse();

        SortedMap<Integer, String[]> ring = new TreeMap<Integer, String[]>();

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
            String[] ids = new String[3];

            for (int k = 0; k < 3; k++) {

                if (driveIndex == nDrives) {
                    driveIndex = 0;
                }

                driveIndex++;

                ids[k] = String.valueOf(driveIndex);
            }

            ring.put(Integer.valueOf(i), ids);
        }

        ringResponse.setRing(ring);

        System.out.println("** response: " + ringResponse.toJson());

        return ringResponse;
    }

}
