package com.seagate.kinetic.tools.management.rest.service.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import kinetic.admin.KineticLogType;

import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogRequest;
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

            String[] useSsl = params.get("useSsl");
            if (useSsl != null) {
                request.setUseSsl(Boolean.parseBoolean(useSsl[0]));
            }

            String[] type = params.get("logType");
            if (type != null) {
                request.setLogType(KineticLogType.valueOf(type[0].toUpperCase()));
            }
        }
    }

}
