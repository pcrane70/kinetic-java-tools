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
package com.seagate.kinetic.tools.management.rest.service.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.setpin.SetErasePinRequest;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * Set erase pin service handler.
 * 
 * @author chiaming
 *
 */
public class SetErasePinHandler extends GenericServiceHandler implements
        ServiceHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return SetErasePinRequest.class;
    }

    @Override
    protected void transformRequestParams(HttpServletRequest httpRequest,
            RestRequest req) {
        if (httpRequest.getContentLength() <= 0) {

            SetErasePinRequest request = (SetErasePinRequest) req;

            Map<String, String[]> params = httpRequest.getParameterMap();

            String[] oldpin = params.get("oldpin");
            if (oldpin != null) {
                request.setOldPin(oldpin[0]);
            }

            String[] newpin = params.get("newpin");
            if (newpin != null) {
                request.setNewPin(newpin[0]);
            }

        }
    }

}
