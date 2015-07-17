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
package com.seagate.kinetic.tools.management.rest.client;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;

import com.seagate.kinetic.tools.external.ExternalResponse;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.util.MessageUtil;

/**
 * Kinetic Rest Client.
 * <p>
 * Applications use this to send kinetic rest requests to kinetic rest service.
 * 
 * @author chiaming
 *
 */
public class KineticRestClient {

    public static final Logger logger = Logger
            .getLogger(KineticRestClient.class.getName());

    public static final String CONTENT_TYPE = "application/json; charset=utf-8";

    // to be used
    @SuppressWarnings("unused")
    private RestClientConfiguration config = null;

    // http client
    private HttpClient client = null;

    // default time out
    private long timeout = 30000;

    /**
     * Instantiate a new instance of Kinetic rest client with default
     * configuration.
     * 
     * @throws Exception
     *             if unable to create a new instance of client.
     */
    public KineticRestClient() throws Exception {
        this.init();
    }

    /**
     * Instantiate a new instance of kinetic rest client with specified
     * configuration.
     * 
     * @throws Exception
     *             if unable to create a new instance of client.
     */
    public KineticRestClient(RestClientConfiguration config) throws Exception {

        // init client config
        this.config = config;

        // init http client
        this.init();
    }

    private void init() throws Exception {
        // new client
        client = new HttpClient();
        // set default timeout
        client.setConnectTimeout(timeout);
        // start the client
        client.start();
    }

    /**
     * Send a rest request to the service.
     * 
     * @param url
     *            the service URL.
     * @param restRequest
     *            the rest request message
     * @return RestResponse message.
     * 
     */
    public RestResponse send(String url, RestRequest restRequest)
            throws RestClientException {

        // rest response message
        RestResponse restResponse = null;

        // create new http request message
        Request request = client.newRequest(url);

        // set http method
        request.method(HttpMethod.POST);

        // create content provider
        StringContentProvider provider = new StringContentProvider(
                CONTENT_TYPE, restRequest.toJson(),
                StandardCharsets.UTF_8);

        // set content provider
        request.content(provider);

        // http response
        ContentResponse httpresp = null;

        try {
            // send http request
            httpresp = request.send();

            // get status
            int status = httpresp.getStatus();

            // if processed
            if (status == 200) {

                // get response content
                String httpBody = httpresp.getContentAsString();

                // get request message type
                MessageType mtype = restRequest.getMessageType();

                // get rest response message
                restResponse = MessageUtil.getResponseMessage(mtype);

                if (MessageType.EXTERNAL_REQUEST != mtype) {
                    // convert json to rest response
                    restResponse = (RestResponse) MessageUtil.fromJson(httpBody,
                            restResponse.getClass());
                } else {
                    // set response message body
                    ((ExternalResponse) restResponse).setResponseMessage(httpBody);
                }
            } else {
                // request cannot be processed
                throw new RestClientException("http response status: " + status
                        + ", reason: " + httpresp.getReason());
            }
        } catch (RestClientException rce) {
            throw rce;
        } catch (Exception e) {
            throw new RestClientException(e);
        }

        return restResponse;
    }

    /**
     * close rest client and release resources.
     */
    public void close() {
        try {
            this.client.stop();
            this.client.destroy();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
