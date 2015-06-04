package com.seagate.kinetic.tools.management.rest.service.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seagate.kinetic.tools.management.rest.message.RestRequest;
import com.seagate.kinetic.tools.management.rest.message.checkversion.CheckVersionRequest;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

public class CheckVersionHandler extends GenericServiceHandler implements
        ServiceHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return CheckVersionRequest.class;
    }

    @Override
    protected void transformRequestParams(HttpServletRequest httpRequest,
            RestRequest req) {

        if (httpRequest.getContentLength() <= 0) {

            CheckVersionRequest request = (CheckVersionRequest) req;

            Map<String, String[]> params = httpRequest.getParameterMap();

            // compatible to CLI (uses this option -v)
            String[] version = params.get("v");
            if (version != null) {
                request.setExpectFirmwareVersion(version[0].toUpperCase());
            }
        }
    }
}
