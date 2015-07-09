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
 * The command service interface is to be implemented by the command service
 * provider.
 * <p>
 * The command service implementation must provide a no-arg constructor.
 * <p>
 * 
 * @author chiaming
 *
 */
public interface ExternalCommandService {

    /**
     * Execute command with the specified message.
     * 
     * @param message
     *            the message obtained form http message body.
     * 
     * @return the execution result.
     */
    public ExternalResponse execute(ExternalRequest message);
}
