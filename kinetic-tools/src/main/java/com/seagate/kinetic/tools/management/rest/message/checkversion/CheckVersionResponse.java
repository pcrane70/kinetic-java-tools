package com.seagate.kinetic.tools.management.rest.message.checkversion;

import java.util.ArrayList;
import java.util.List;

import com.seagate.kinetic.tools.management.rest.message.DeviceStatus;
import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestResponse;

/**
 * Check version response message.
 * 
 * @author chiaming
 *
 */
public class CheckVersionResponse extends RestResponse {

    /**
     * The status code is set to HttpServletResponse.SC_EXPECTATION_FAILED if
     * version is not the expected firmware version.
     * 
     * The status message contains the firmware version of the device.
     */
    protected List<DeviceStatus> devices = new ArrayList<DeviceStatus>();

    public CheckVersionResponse() {
        super.setMessageType(MessageType.CHECKVERSION_REPLY);
    }

    public void setDevices(List<DeviceStatus> devices) {
        this.devices = devices;
    }

    public List<DeviceStatus> getDevices() {
        return this.devices;
    }
}
