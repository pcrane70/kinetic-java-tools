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
package com.seagate.kinetic.tools.management.rest.client.ping;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.ping.PingRequest;

/**
 * 
 * ping devices with the specified device ids.
 * 
 * @author chiaming
 *
 */
public class PingClientExample2 {

    public static RestRequest createPingRequest(String ip) {

        // construct ping request message
        PingRequest request = new PingRequest();

        // set example device ids
        List<DeviceId> devices = new ArrayList<DeviceId>();

        DeviceId did = new DeviceId();
        String[] ips = { ip };
        did.setIps(ips);
        devices.add(did);

        // set to request message
        request.setDevices(devices);

        // return request message
        return request;
    }

    public static void main(String[] args) throws Exception {

        KineticRestClient client = new KineticRestClient();

        // default url
        String url = "http://localhost:8080/ping";

        // override with arg[0]
        if (args.length > 0) {
            url = args[0];
        }

        // default device ip to ping
        String ip = "127.0.0.1";

        // override with args[1]
        if (args.length > 1) {
            ip = args[1];
        }

        // create ping request message
        RestRequest request = createPingRequest(ip);

        // send request
        RestResponse response = client.send(url, request);

        // print response
        System.out.println(response.toJson());

        // close client
        client.close();
    }

}
