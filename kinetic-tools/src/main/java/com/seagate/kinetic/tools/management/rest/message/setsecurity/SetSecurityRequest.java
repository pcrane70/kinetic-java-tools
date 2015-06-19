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
package com.seagate.kinetic.tools.management.rest.message.setsecurity;

import java.util.ArrayList;
import java.util.List;

import kinetic.admin.ACL;

import com.seagate.kinetic.tools.management.rest.message.MessageType;
import com.seagate.kinetic.tools.management.rest.message.RestRequest;

public class SetSecurityRequest extends RestRequest {

    private List<ACL> acl = new ArrayList<ACL>();

    public SetSecurityRequest() {
        setMessageType(MessageType.SET_SECURITY);
        setUseSsl(true);
    }

    public void setAcl(List<ACL> acl) {
        this.acl = acl;
    }

    public List<ACL> getAcl() {
        return this.acl;
    }

}
