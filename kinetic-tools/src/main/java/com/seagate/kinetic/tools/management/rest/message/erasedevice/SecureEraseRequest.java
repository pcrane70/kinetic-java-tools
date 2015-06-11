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
package com.seagate.kinetic.tools.management.rest.message.erasedevice;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;

public class SecureEraseRequest extends RestRequest {
	private static final int REQUEST_TIMEOUT_IN_SECONDS = 180;

    private String erasepin = null;

    public SecureEraseRequest() {
        setMessageType(MessageType.SECURE_ERASE);
        setRequestTimeout(REQUEST_TIMEOUT_IN_SECONDS);
		setUseSsl(true);
    }

    public void setPin(String pin) {
        this.erasepin = pin;
    }

    public String getPin() {
        return this.erasepin;
    }
}
