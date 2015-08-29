/**
 * 
 */
package com.seagate.kinetic.tools.external.swift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;



/**
 * @author mshafiq
 *
 */
public class Ring implements ExternalCommandService {
	class pair {
		String K;
		String V;
		public pair (String K, String V) {
			this.K = K;
			this.V = V;
		}
	}
	class device {
		String id;
		List<pair> plist;
		public device(List<pair> plist){
			this.plist = plist;
		}
	}
	public static final Logger logger = Logger.getLogger(Ring.class.getName());
	 		public Ring() {}
	   	    @Override
    public ExternalResponse execute(ExternalRequest req) {

	   	    	SwiftRequest request = (SwiftRequest) req;
	   	    	ExternalResponse resp = new ExternalResponse();
	   	    	List<pair> list = new ArrayList<pair>();

	   	        System.out.println("** received request: " + request.toJson());
	   	        CommandFilter filt = CommandFilter.getInstance();
	   	        String Cmd  = filt.GetRingCommand(request.getResource());
	   	        String file = request.getFile();
	   	        if (file == null) file = Globals.GetSwiftRingFile(request);
	   	        String dir = request.getDir();
	   	        if (dir == null) dir = Globals.GetSwiftDir(request);
	   	        Cmd += ("," + file);
		        String rc =  filt.ExecShellCmd(Cmd, dir);
		        resp.setResponseMessage(String2Json(rc));
		        
		        return resp;	
	   	    }
	   	    
	  private String String2Json(String in)
	  {
		  String[] splits = in.split("Devices:");
		  splits = splits[1].split("\n");
		  String heading = splits[0].trim();
		  heading = heading.replace("ip address", "ipaddress");
		  heading = heading.replace("replication ip", "repliactionip");
		  heading = heading.replace("replication port", "repliactionport");
		  String[] titles = heading.split("\\s+");
		  List<List<Map<String, String>>> ring = new ArrayList<List<Map<String, String>>>();
		  for (String key: titles) key = key.trim();
		  for (int i = 1; i < splits.length; i++) {
			  String info = splits[i];
			  List<Map<String, String>> dev = new ArrayList<Map<String, String>>();
			  StringTokenizer st = new StringTokenizer(info);
			  List<pair> plist = new ArrayList<pair>();
			  int j = 0;
			  while(st.hasMoreTokens() && j < titles.length) {
				  Map map = new HashMap();
				  map.put(titles[j++], st.nextToken());
				  dev.add(map);	  
			  }
			  ring.add(dev);	  
		   }
		  Gson gson = new Gson();
	      return (gson.toJson(ring));
	  }
	   	    
}
