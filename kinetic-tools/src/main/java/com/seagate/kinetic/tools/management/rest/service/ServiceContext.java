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
package com.seagate.kinetic.tools.management.rest.service;

import javax.servlet.http.HttpServletRequest;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;

/**
 * 
 * Rest request service context.
 * 
 * @author chiaming
 */
public class ServiceContext {

    private MessageType mtype = null;

    private RestRequest request = null;

    private RestResponse response = null;

    private byte[] value = null;

    private HttpServletRequest httpServletRequest = null;

    public ServiceContext(HttpServletRequest servletRequest) {
        this.httpServletRequest = servletRequest;
    }

    public MessageType getRequestMessageType() {
        return this.mtype;
    }

    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    public void setRequestMessage(RestRequest request) {
        this.request = request;
    }

    public RestRequest getRequestMessage() {
        return this.request;
    }

    public void setResponseMessage(RestResponse response) {
        this.response = response;
    }

    public RestResponse getResponseMessage() {
        return this.response;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return this.value;
    }

}
