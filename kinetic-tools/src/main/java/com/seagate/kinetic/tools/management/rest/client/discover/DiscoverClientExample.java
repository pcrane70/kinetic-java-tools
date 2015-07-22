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
package com.seagate.kinetic.tools.management.rest.client.discover;

import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverRequest;

public class DiscoverClientExample {

    public static void main(String[] args) throws Exception {

        KineticRestClient client = new KineticRestClient();

        // default url
        String url = "http://localhost:8080/discover";

        // override with arg[0]
        if (args.length > 0) {
            url = args[0];
        }

        DiscoverRequest request = new DiscoverRequest();
        request.setTimeout(10);
        request.setScoped(true);
        request.setStartIp("127.0.0.1");
        request.setEndIp("127.0.0.100");

        // send request
        RestResponse response = client.send(url, request);

        // print response
        System.out.println(response.toJson());

        // close client
        client.close();
    }


}
