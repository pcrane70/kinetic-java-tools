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
package com.seagate.kinetic.tools.management.rest.message.ping;

import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

/**
 * This generates a simple example of the <code>PingRequest</code> message.
 * <p>
 * The disco Id is set in the Ping request message. The disco Id is obtained
 * from a prior <code>DiscoverResponse</code> message.
 * 
 */
public class PingRequestExample2 {

    public static void main(String[] args) {

        PingRequest req = new PingRequest();

        req.setDiscoId("12345");

        String request = req.toJson();

        System.out.println(request);

        PingRequest req2 = (PingRequest) MessageUtil.fromJson(request,
                PingRequest.class);

        String request2 = req2.toJson();

        System.out.println(request2);

    }

}
