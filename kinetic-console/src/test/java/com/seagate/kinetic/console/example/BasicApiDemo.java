/**
 * 
 * Copyright (C) 2014 Seagate Technology.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.seagate.kinetic.console.example;

import java.io.UnsupportedEncodingException;

import kinetic.client.ClientConfiguration;
import kinetic.client.Entry;
import kinetic.client.KineticClient;
import kinetic.client.KineticClientFactory;
import kinetic.client.KineticException;

public class BasicApiDemo {

    // String to byte[] encoding
    public static final String UTF8 = "utf8";

    // kinetic client
    private KineticClient client = null;

    /**
     * Start the async API usage example.
     * 
     * @throws KineticException
     *             if any Kinetic internal error occurred.
     * @throws InterruptedException
     *             if the example is interrupted before it is completed.
     */
    public void runExample() throws KineticException, InterruptedException {

        // Client configuration and initialization
        ClientConfiguration clientConfig = new ClientConfiguration();

        clientConfig.setPort(18123);

        client = KineticClientFactory.createInstance(clientConfig);

        // initial key, value and new version
        byte[] key1 = stringToBytes("hello1");
        byte[] value1 = stringToBytes("world1");

        // create entry
        Entry simpleEntry1 = new Entry(key1, value1);

        // put two entries
        int i = 0;

        while (true) {
            i++;
            // forced put two entries
            client.putForced(simpleEntry1);
            System.out.println("forced put entry: key="
                    + new String(key1) + ", value=" + new String(value1));

            if (i % 2 == 0) {
                // get entry, expect to receive hello1 entry
                Entry entryHello1 = client.get(key1);
                System.out.println("get the entry of key=" + new String(key1)
                        + ", value=" + new String(entryHello1.getValue()));
            }

            // forced delete key1
            if (i % 3 == 0) {
                client.deleteForced(key1);
                System.out.println("forced delete entry: key="
                        + new String(key1));
            }

            Thread.sleep(1000);
        }

        // close kinetic client
        // this.client.close();
    }

    /**
     * convert string to byte[] using UTF8 encoding.
     * 
     * @param string
     *            string to be converted to byte[].
     * 
     * @return the byte[] representation of the specified string
     */
    private static byte[] stringToBytes(String string) {

        try {
            return string.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws KineticException,
            InterruptedException {
        BasicApiDemo syncUsage = new BasicApiDemo();

        syncUsage.runExample();
    }
}
