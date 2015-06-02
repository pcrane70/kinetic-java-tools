package com.seagate.kinetic.tools.management.rest.service.handler;

import com.seagate.kinetic.tools.management.rest.message.getlog.GetLogRequest;
import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

public class GetLogHandler extends GenericServiceHandler implements
        ServiceHandler {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getRequestMessageClass() {
        return GetLogRequest.class;
    }

}
