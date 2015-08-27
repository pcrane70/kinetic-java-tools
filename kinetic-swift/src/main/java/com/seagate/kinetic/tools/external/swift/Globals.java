/**
 * 
 */
package com.seagate.kinetic.tools.external.swift;

import com.seagate.kinetic.tools.external.ExternalRequest;

/**
 * @author mshafiq
 *
 */
public class Globals {
	static public String SWIFT_DB_EXTENSION = ".db";
	static public String SWIFT_DIR = "/etc/swift";
	static public String SWIFT_DATA_DIR = "/swift";
	static public String SWIFT_OBJECT_RING_BUILDER_FILE = "object.builder";
	static public String SWIFT_CONTAINER_RING_BUILDER_FILE = "container.builder";
	static public String SWIFT_ACCOUNT_RING_BUILDER_FILE = "account.builder";
	static public String SWIFT_OBJECT_CONF_FILE = "object.conf";
	static public String SWIFT_CONTAINER_CONF_FILE = "container.conf";
	static public String SWIFT_ACCOUNT_CONF_FILE = "account.conf";
	static public String SWIFT_PROXY_CONF_FILE = "proxy.conf";
	static public String SWIFT_DISPERSION_CONF_FILE = "dispersion.conf";
	static public String SWIFT_USER = "user";
	static public String SWIFT_KEY = "key";
	static public String SWIFT_PASSWORD = "password";
	static public final String SWIFT_ENV_DIR = "SWIFT_DIR";
	static public final String SWIFT_OBJECT_BUILDER_FILE = "object.builder";
	static public final String SWIFT_CONTAINER_BUILDER_FILE = "container.builder";
	static public final String SWIFT_ACCOUNT_BUILDER_FILE = "account.builder";
	static public final String SWIFT_PROXY_BUILDER_FILE = "proxy.builder";
	static public final String SWIFT_OBJECT_REQUEST = "object";
	static public final String SWIFT_CONTAINER_REQUEST = "container";
	static public final String SWIFT_ACCOUNT_REQUEST = "account";
	static public final String SWIFT_PROXY_REQUEST = "proxy";
	static public final String SWIFT_GET_NODES = "swift-get-nodes";
	static public final String SWIFT_COMMAND = "swift";
	static public final String IPMI_USER = "ADMIN";
	static public final String IPMI_PASSWORD = "ADMIN";
	static public final String IPMI_HOST = "192.168.1.99";
	static public final int IPMI_MAX_DRIVES = 12;
	static public final int IPMI_MAX_DATA = 4096;
	static public final int IPMI_PING_TIMEOUT = 1000;
	static public final String IPMI_DIR = "/usr/local/bin";
	static public final int  IPMI_SM_CHASSIS_RESPONSE = 284;
	static public final int IPMI_PORT = 623;
	static public final int  KINETIC_DRIVE_PORT = 8123;
    static public final int  KINETIC_DRIVE_TLS_PORT = 8443;
    static public final String IPMI_SUPERMICRO_TAG = "supermicro";
    static public final String IPMI_SUPERMICRO_PART = "K1048-RT";
    static public final int  IPMI_UDP_PORT = 9125;
    
	
    static public String GetSwiftDir(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

        String dir = request.getDir();
        if (dir == null)
            dir = Globals.SWIFT_DIR;
        return dir;
    }

    static public String GetSwiftRingFile(ExternalRequest req) {
        SwiftRequest request = (SwiftRequest) req;

        switch (request.getResource()) {
        case SWIFT_OBJECT_REQUEST:
            return SWIFT_OBJECT_BUILDER_FILE;
        case SWIFT_CONTAINER_REQUEST:
            return SWIFT_CONTAINER_BUILDER_FILE;
        case SWIFT_ACCOUNT_REQUEST:
            return SWIFT_ACCOUNT_BUILDER_FILE;
        case SWIFT_PROXY_REQUEST:
            return SWIFT_PROXY_BUILDER_FILE;

        }
        return null;
    }
	
	
}
