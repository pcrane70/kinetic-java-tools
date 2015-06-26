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

import kinetic.admin.KineticLogType;

import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogRequest;
import com.seagate.kinetic.tools.management.rest.message.getlog.LogTypeForCliComptibility;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

public class GetLogHandler extends GenericServiceHandler implements
        ServiceHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return GetLogRequest.class;
    }

    @Override
    protected void transformRequestParams(HttpServletRequest httpRequest,
            RestRequest req) {

        if (httpRequest.getContentLength() <= 0) {

            GetLogRequest request = (GetLogRequest) req;

            Map<String, String[]> params = httpRequest.getParameterMap();

            String[] useSsl = params.get("usessl");
            if (useSsl != null) {
                request.setUseSsl(Boolean.parseBoolean(useSsl[0]));
            }

            String[] type = params.get("type");

            KineticLogType logType = getLogType(type);

            if (type != null) {
                request.setLogType(logType);
            }

            String[] name = params.get("name");
            if (name != null) {
                request.setName((name[0]));
            }
        }
    }

    /**
     * Get log type. Param options also accept those used in CLI options.
     * <p>
     * The log type used in the REST request uses the names defined in the
     * .proto file.
     * 
     * @param type
     *            param options
     * @return KineticLogType
     */
    private static KineticLogType getLogType(String[] type) {
        KineticLogType logType = null;

        if (type == null) {
            return null;
        }

        // try if it is defined in KineticLogType
        try {
            logType = KineticLogType.valueOf(type[0].toUpperCase());

            // found logtype
            return logType;
        } catch (Exception e) {
            ;
        }

        // for backward compatibility, check if it is defined in CLI names
        try {

            // get from CLI logtype option
            LogTypeForCliComptibility t = LogTypeForCliComptibility
                    .valueOf(type[0].toUpperCase());
            
            // cli string name
            String cliString = t.toString();

            // param string name
            String paramString = null;

            if (cliString.equals("CAPACITY")) {
                paramString = KineticLogType.CAPACITIES.toString();
            } else {
                // try plural form of the type
                paramString = cliString + "S";
            }

            logType = KineticLogType.valueOf(paramString);
            
        } catch (Exception e) {
            ;
        }

        return logType;
    }

}
