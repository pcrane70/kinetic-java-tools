package com.seagate.kinetic.tools.external.swift;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;
import com.seagate.kinetic.tools.management.rest.message.DeviceId;
import com.seagate.kinetic.tools.management.rest.message.hwview.Chassis;
import com.seagate.kinetic.tools.management.rest.message.hwview.Device;
import com.seagate.kinetic.tools.management.rest.message.hwview.Rack;



public class SuperStore implements ExternalCommandService {
	public static final Logger logger = Logger.getLogger(CommandFilter.class.getName());
		public SuperStore() {}
	    @Override
    public ExternalResponse execute(ExternalRequest req) {
	    	ExternalResponse resp = new ExternalResponse();

        SwiftRequest request = (SwiftRequest) req;
        

	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        String dir = request.getDir();
	        String user = request.getUser();
	        String password = request.getPassword();
	        String host = request.getHost();
	        if (dir == null ) dir = Globals.IPMI_DIR;
	        if (user == null)  user = Globals.IPMI_USER;
	        if (password == null)  password = Globals.IPMI_PASSWORD;
	        if (host == null) host=Globals.IPMI_HOST;
	        /*  ipmitool -I lan -U ADMIN -P ADMIN -H 192.168.32.10 raw 0x30 0x70 0x86 1
	         * ipmitool _I {interface} -U {user} –P {password}–H
	         * {Host}  raw 0x30 0x70 0x86 drive_number
	         
	         */
	        String cmd = "ipmitool" + "," + "-Ilan"+  "," + "-U" + user + "," + "-P" + password +
	        		"," +"-H" + host + "," + "raw,0x30,0x70,0x86,";
	        
	        Chassis smChassis = new Chassis();
	        List<Device> devices = new ArrayList<Device>();
	        List<Chassis> chassises = new ArrayList<Chassis>();
	        Rack rack = new Rack();
	        String rc = new String();
	        /*
	         * Data must be 284 bytes at minimum;
	         */
	        for (int i = 1; i <= Globals.IPMI_MAX_DRIVES; i++) {
	        	String drvCmd = cmd + i;
	        	logger.info("executing Comd " + drvCmd + " Dir == " + dir);
	        	if (host.equalsIgnoreCase("sampleChassis")) 
	        		rc = sampleChassis;
	        	else
	        		rc  = filt.ExecShellCmd(drvCmd, dir);
	        	/* remove white characters*/
	        	rc = rc.replaceAll("\\s","");
	        	
	        	if (rc.length() < Globals.IPMI_SM_CHASSIS_RESPONSE) {
	        		logger.info(drvCmd + " returning invalid response");
	        	}
	        	else {
	        		PrintDriveInfo(rc);
	        		String[] ips = {getIP1(rc), getIP2(rc) };
	        		Device device = new Device();
	        		DeviceId deviceId = new DeviceId();
	        		deviceId.setPort(Globals.KINETIC_DRIVE_PORT);
	        		deviceId.setTlsPort(Globals.KINETIC_DRIVE_TLS_PORT);
	        		deviceId.setWwn(getWWID(rc));
	        		deviceId.setIps(ips);
	        		device.setDeviceId(deviceId);        		
	        		devices.add(device);
	       
	        	}
	        	smChassis.setDevices(devices);
	        	
	        }
	       // byte[] bytes = rc.getBytes();
	        chassises.add(smChassis);
	        rack.setChassis(chassises);
	        Gson gson = new Gson();
	        
			resp.setResponseMessage(gson.toJson(rack));
		    return resp;
	        
	    }
	    private void PrintDriveInfo(String in)
	    {
	    	logger.info(" WWID:" + getWWID(in)  +
	    			    " Mac-1:" + getMAC1(in) +
	    			    " IP-1:" + getIP1(in) +
	    			    " Mac-2:" + getMAC2(in) +
	    			    " IP-2:" + getIP2(in));
	    	
	    }
	    /* bytes 1 to 8 */
	    private String getWWID(String in)
	    {
	    	return in.substring(1 * 2, 9 * 2);
	    }
	    
	    /* bytes 265 to 270 */
	    private String getMAC1(String in)
	    {
	    	return in.substring(265 * 2, 271 * 2);
	    }
	    private String Convert2IP(String in)
	    {
	    	String IP =  new String();
	    	int i = 0;
	    	while (i < 4) {
	    		if (IP.length() > 0) IP += ".";
	    		String rc = in.substring(i * 2, (++i) * 2);
	    		IP += String.valueOf(Integer.parseInt(rc, 16));
	    		
	    	}
	    	return IP;
	    	
	    }
	    /* bytes 132 to 135 */
	    private String getIP1(String in)
	    {
	    	return Convert2IP(in.substring(132 * 2, 136  * 2));
	   
	    }
	    
	    /* bytes 271 to 276 */
	    private String getMAC2(String in)
	    {
	    
	    	return in.substring(271 * 2, 277 * 2);
	    }
	    
	    /* bytes 201 to 204 */
	    private String getIP2(String in)
	    {
	    	return Convert2IP(in.substring(201 * 2, 205  * 2));
	    }
	   
	    
	    public static String sampleChassis =  
	    		"01 50 00 c5 00 79 87 e7 98 00 00 00 00 00 00 00" + 
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
	    		"00 00 00 1c 00 00 00 00 00 00 02 06 00 00 01 02" +
	    		"04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
	    		"00 00 53 65 61 67 61 74 65 00 00 00 00 00 00 00" +
	    		"00 00 53 54 34 30 30 30 4e 4b 30 30 31 2d 31 4e" +
	    		"58 36 15 ac 10 00 01 ff ff 00 00 00 00 00 00 00" +
	    		"00 00 00 00 ac 10 11 46 ff ff 00 00 00 00 00 00" +
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
	    		"00 00 00 00 00 00 00 15 ac 11 00 01 ff ff 00 00" +
	    		"00 00 00 00 00 00 00 00 00 ac 11 02 8f ff ff 00" +
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" + 
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
	    		"00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00" +
	    		"01 00 00 00 00 00 00 00 00 00 11 c6 02 24 cc 00" +
	    		"11 c6 02 24 cd 01 01 03 00 05 00 00";
	        
}