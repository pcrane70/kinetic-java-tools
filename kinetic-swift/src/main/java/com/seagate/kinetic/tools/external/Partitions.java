/**
 * 
 */
package com.seagate.kinetic.tools.external;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author mshafiq
 *
 */
public class Partitions implements ExternalCommandService {
	public static final Logger logger = Logger.getLogger(CommandFilter.class.getName());
		public Partitions() {}
	    @Override
	    public ExternalResponse execute(ExternalRequest request) {
	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        String dir = request.getDir();
	        String file = request.getFile();
	        String cmd = filt.GetRingCommand(request.getResource());
	        if (file == null) file = Globals.SWIFT_OBJECT_BUILDER_FILE;
	        if (dir == null ) dir = Globals.SWIFT_DIR;
	        logger.info("executing Comd " + cmd + " Dir == " + dir + " File == " + file );
	        String rc = filt.ExecShellCmd(cmd, dir);
	        int devices = Key2Val(rc, "devices"); 
	        logger.info("extracting partition info for " + devices + "  devices");
	        String msg = null;
	        cmd = filt.GetPartitionCommand(request.getResource());
	        for (int i = 0; i < devices; i++) {
	        	String listCmd = cmd + "," + file + "," + "list_parts" + "," + "d" + i;
	        	logger.info("executing Command " + listCmd + " Directory == " + dir + " File == " + file );
	        	rc = filt.ExecShellCmd(listCmd, dir);
	        	BuildPartMap(rc, Integer.toString(i));
	        }
	        ExternalResponse resp = new ExternalResponse();
			resp.setResponseMessage(PartMap2Str());
	        return resp;
	    }
	    
	    private int Key2Val(String key, String pattern)
	    {
	    	String[] rc = key.split(pattern);
	    	StringTokenizer st = new StringTokenizer(rc[0]);
	    	String lastToken = null;
	    	while(st.hasMoreTokens()) 
	    		lastToken = st.nextToken();
	    	return Integer.parseInt(lastToken);
	    }
	    private void BuildPartMap(String in, String id)
	    {
	    	in = in.split("Matches")[1];
	    	StringTokenizer st = new StringTokenizer(in);
	    	while (st.hasMoreTokens()) {
	    		String part = st.nextToken();
	    		String count = st.nextToken();
	    		String dev = partMap.get(Integer.parseInt(part));
	    		if (dev == null) dev = id;
	    		else dev += ("," + id);
	    		logger.info("Updating Map Partition " + part + " for device " + dev + " Count " + count);
	    		partMap.put(Integer.parseInt(part), dev);
	    	}
	    }
	    private String PartMap2Str()
	    {
	    	Iterator it = partMap.entrySet().iterator();
	    	String rc = new String();
	        while (it.hasNext()) {
	            Map.Entry pair = (Map.Entry)it.next();
	            rc += (pair.getKey() + " = " + pair.getValue()) + "\n";
	        }	
	        logger.info("Partition List:" + rc);
	        return rc;
	    }
	    
	    
	    Map<Integer, String> partMap = new TreeMap<Integer, String>();
	    
	    

}