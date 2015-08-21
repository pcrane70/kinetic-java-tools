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

import com.seagate.kinetic.tools.management.rest.client.KineticRestClient;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;

/**
 * example client to invoke kinetic swift command service.
 * 
 * @author chiaming
 *
 */
public class SwiftClientExample {

    public static void main(String[] args) throws Exception {

        KineticRestClient client = new KineticRestClient();

        try {

            // external service URL
            String url = "http://localhost:8080/kinetic/swift?class=HelloSwift";

            // new ping request message
            SwiftRequest request = new SwiftRequest();

            request.setPartition("123");

            request.setDir("/tmp");

            request.setFile("foo");

            request.setSwiftKey("swiftKey");

            request.setUrl("swift url");

            // set request message
            request.setResource("Hello Swift, a=b, c=d");

            // send request
            RestResponse response = client.send(url, request);

            // print response
            System.out.println(response.toJson());

        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
