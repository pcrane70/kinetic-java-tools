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
package com.seagate.kinetic.tools.management.common;

public class KineticToolsException extends Exception {
    private static final long serialVersionUID = 1312829893477267445L;

    public KineticToolsException() {
    }

    public KineticToolsException(String message) {
        super(message);
    }

    public KineticToolsException(Throwable cause) {
        super(cause);
    }

    public KineticToolsException(String message, Throwable cause) {
        super(message, cause);
    }
}
