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
package com.seagate.kinetic.tools.management.rest.message.util;

import com.google.gson.Gson;
import com.seagate.kinetic.tools.external.ExternalResponse;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;
import com.seagate.kinetic.tools.management.rest.message.checkversion.CheckVersionResponse;
import com.seagate.kinetic.tools.management.rest.message.discover.DiscoverResponse;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.InstantEraseResponse;
import com.seagate.kinetic.tools.management.rest.message.erasedevice.SecureEraseResponse;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogResponse;
import com.seagate.kinetic.tools.management.rest.message.ping.PingResponse;
import com.seagate.kinetic.tools.management.rest.message.setclversion.SetClusterVersionResponse;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetErasePinResponse;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetLockPinResponse;
import com.seagate.kinetic.tools.management.rest.message.setsecurity.SetSecurityResponse;

/**
 * Rest Json message utilities.
 * 
 * @author chiaming
 */
public class MessageUtil {

    /**
     * Convert Json string message to its corresponding Java container.
     * 
     * @param json
     *            json message
     * @param clazz
     *            the Class instance of the json message's corresponding Java
     *            container.
     * @return the Class instance of the json message's corresponding Java
     *         container.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object fromJson(String json, Class clazz) {

        /**
         * Gson instance.
         */
        Gson gson = new Gson();

        /**
         * convert json to its corresponding Java container instance.
         */
        return gson.fromJson(json, clazz);
    }

    public static RestResponse getResponseMessage(MessageType mtype) {
        RestResponse response = null;

        switch (mtype) {
        case DISCOVER:
            response = new DiscoverResponse();
            break;
        case PING:
            response = new PingResponse();
            break;
        case GETLOG:
            response = new GetLogResponse();
            break;
        case CHECKVERSION:
            response = new CheckVersionResponse();
            break;
        case SET_ERASEPIN:
            response = new SetErasePinResponse();
            break;
        case SET_LOCKPIN:
            response = new SetLockPinResponse();
            break;
        case INSTANT_ERASE:
            response = new InstantEraseResponse();
            break;
        case SECURE_ERASE:
            response = new SecureEraseResponse();
            break;
        case SET_SECURITY:
            response = new SetSecurityResponse();
            break;
        case SET_CLVERSION:
            response = new SetClusterVersionResponse();
            break;
        case EXTERNAL_REQUEST:
            response = new ExternalResponse();
            break;
        default:
            throw new java.lang.UnsupportedOperationException(mtype.name());
        }

        return response;
    }

}
