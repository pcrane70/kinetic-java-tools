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
package com.seagate.kinetic.tools.external;


/**
 * Hello world kinetic tools swift example.
 * 
 * @author chiaming
 *
 */
public class Hello implements ExternalCommandService {

    public Hello() {
        ;
    }

    @Override
    public ExternalResponse execute(ExternalRequest request) {

        System.out.println("** received request: " + request.toJson());

        ExternalResponse resp = new ExternalResponse();
        resp.setResponseMessage("hello external response");

        return resp;
    }

}
