package com.seagate.kinetic.tools.management.rest.message.checkversion;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;

public class CheckVersionRequest extends RestRequest {

    private String expectFirmwareVersion = "2.7.3";

    public CheckVersionRequest() {
        super.setMessageType(MessageType.CHECKVERSION);
    }

    public void setExpectFirmwareVersion(String v) {
        this.expectFirmwareVersion = v;
    }

    public String getExpectFirmwareVersion() {
        return this.expectFirmwareVersion;
    }

}
