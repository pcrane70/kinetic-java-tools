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
package com.seagate.kinetic.tools.management.rest.service.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.seagate.kinetic.tools.management.rest.service.ServiceHandler;

/**
 * 
 * @author chiaming
 */
public class HandlerMap {

    private static final Logger logger = Logger.getLogger(HandlerMap.class
            .getName());

    // ping request
    public static final String PING = "/ping";

    // error handler
    public static final String ERROR = "/error";

    // discover request
    public static final String DISCOVER = "/discover";

    // getlog request
    public static final String GETLOG = "/getlog";

    // check version
    public static final String CHECKVERSION = "/checkversion";

    // set erase pin
    public static final String SETERASEPIN = "/seterasepin";

    // set lock pin
    public static final String SETLOCKPIN = "/setlockpin";

    // instant erase
    public static final String INSTANTERASE = "/instanterase";

    // secure erase
    public static final String SECUREERASE = "/secureerase";

    // lock device
    public static final String LOCKDEVICE = "/lockdevice";

    public static final String UNLOCKDEVICE = "/unlockdevice";

    // set cluster version
    public static final String SETCLUSTERVERSION = "/setclusterversion";

    // set security (acl)
    public static final String SETSECURITY = "/setsecurity";
    
    // get hardware configuration view
    public static final String HARDWAREVIEW = "/hwview";
    public static final String CONFIG = "/config";

    // external command handler
    public static final String EXTERNAL = "/external";

    public static final String SWIFT = "/swift";

    // handler map
    private static ConcurrentHashMap<String, ServiceHandler> hmap = new ConcurrentHashMap<String, ServiceHandler>();
    
    private static HardwareViewHandler ConfigHandler = new HardwareViewHandler();

    static {
        hmap.put(PING, new PingHandler());
        hmap.put(ERROR, new ErrorHandler());
        hmap.put(DISCOVER, new DiscoverHandler());
        hmap.put(GETLOG, new GetLogHandler());
        hmap.put(CHECKVERSION, new CheckVersionHandler());
        
        hmap.put(SETERASEPIN, new SetErasePinHandler());
        hmap.put(SETLOCKPIN, new SetLockPinHandler());

        hmap.put(INSTANTERASE, new InstantEraseHandler());

        hmap.put(SECUREERASE, new SecureEraseHandler());

        hmap.put(LOCKDEVICE, new LockDeviceHandler());

        hmap.put(UNLOCKDEVICE, new UnLockDeviceHandler());

        hmap.put(SETCLUSTERVERSION, new SetClusterVersionHandler());

        hmap.put(SETSECURITY, new SetSecurityHandler());
        
        // register hardware view/config handler
        hmap.put(HARDWAREVIEW, ConfigHandler);
        hmap.put(CONFIG, ConfigHandler);

        /**
         * XXX chiaming 08/11/2015: backward compatible for swift commands. This
         * can be fixed once swift command path in the doc is fixed.
         */
        hmap.put(EXTERNAL, new ExternalCommandHandler());
        hmap.put(SWIFT, new SwiftCommandHandler());
    }

    /**
     * find handler based on the request URI
     * 
     * @param path
     *            request URI
     * @return the matched service handler., or ErrorHandler if not found.
     */
    public static ServiceHandler findHandler(String path) {

        logger.info("*** path=" + path);

        // get handler relative path
        int index = path.lastIndexOf("/");
        String hp = path.substring(index);

        ServiceHandler handler = null;

        handler = hmap.get(hp);

        if (handler == null) {
            logger.warning("cound not find handler for request: " + path);

            handler = hmap.get(ERROR);
        } else {
            logger.info("found handler: " + handler.getClass().getName());
        }

        return handler;
    }
    
}
