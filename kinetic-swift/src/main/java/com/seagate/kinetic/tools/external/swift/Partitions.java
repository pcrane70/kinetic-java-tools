/**
 * 
 */
package com.seagate.kinetic.tools.external.swift;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;
import com.seagate.kinetic.tools.external.swift.ring.Opartition;

/**
 * @author mshafiq
 *
 */
public class Partitions implements ExternalCommandService {
	public static final Logger logger = Logger.getLogger(CommandFilter.class.getName());
		public Partitions() {}
	    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        String dir = request.getDir();
	        String file = request.getFile();
	        String cmd = filt.GetRingCommand(request.getResource());
	        if (file == null) file = Globals.SWIFT_OBJECT_BUILDER_FILE;
	        if (dir == null ) dir = Globals.SWIFT_DIR;
	        logger.info("executing Comd " + cmd + " Dir == " + dir + " File == " + file );
	        cmd = cmd + "," + file;
	        String rc = filt.ExecShellCmd(cmd, dir);
	        int devices = Key2Val(rc, "devices"); 
	        logger.info("extracting partition info for " + devices + "  devices");
	        String msg = null;
	        cmd = filt.GetPartitionCommand(request.getResource());
	        for (int i = 0; i < devices; i++) {
	        	String listCmd = cmd + "," + file + "," + "list_parts" + "," + "d" + i;
	        	logger.info("executing Command " + listCmd + " Directory == " + dir + " File == " + file );
	        	rc = filt.ExecShellCmd(listCmd, dir);
	        	BuildPartMap(rc, i);
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
	    private void BuildPartMap(String in, int id)
	    {
	    	in = in.split("Matches")[1];
	    	StringTokenizer st = new StringTokenizer(in);
	    	while (st.hasMoreTokens()) {
	    		String part = st.nextToken();
	    		String count = st.nextToken();
	    		List<Integer> dev = partMap.get(Integer.parseInt(part));
	    		if (dev == null) 
	    			dev = new ArrayList<Integer>();
	    		dev.add(id);
	    		partMap.put(Integer.parseInt(part), dev);
	    	}
	    }

    private String PartMap2Str() {

        // transform to oRing
        ArrayList<Opartition> oring = new ArrayList<Opartition>();

        Iterator<Integer> it = partMap.keySet().iterator();
        while (it.hasNext()) {

            // get next key
            Integer key = it.next();

            // get drive ids
            List<Integer> ids = partMap.get(key);

            // create partition
            Opartition p = new Opartition();
            // set partition id
            p.setPartitionId(key.intValue());
            // set drive ids
            p.setDriveIds(ids);

            // add partition to ring
            oring.add(p);
        }

        Gson gson = new Gson();

        // to json string for the ring
        return gson.toJson(oring);
    }
	    
	    
	    Map<Integer, List<Integer>> partMap = new TreeMap<Integer, List<Integer>>();
	    
}