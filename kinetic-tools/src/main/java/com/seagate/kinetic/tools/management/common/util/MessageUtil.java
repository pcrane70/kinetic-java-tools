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
package com.seagate.kinetic.tools.management.common.util;

import com.google.gson.Gson;

/**
 * Rest Json message utilities.
 * 
 * @author chiaming
 */
public class MessageUtil {

    /**
     * Convert Json string message to its corresponding Java container.
     * 
     * @param json
     *            json message
     * @param clazz
     *            the Class instance of the json message's corresponding Java
     *            container.
     * @return the Class instance of the json message's corresponding Java
     *         container.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object fromJson(String json, Class clazz) {

        /**
         * Gson instance.
         */
        Gson gson = new Gson();

        /**
         * convert json to its corresponding Java container instance.
         */
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {

        /**
         * Gson instance.
         */
        Gson gson = new Gson();

        /**
         * convert Java container instance to Json.
         */
        return gson.toJson(obj);
    }

}
