/**
 * 
 */
package com.seagate.kinetic.tools.external.swift;

import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;

/**
 * @author mshafiq
 *
 */
public class Nodes implements ExternalCommandService {
		public Nodes() {}
	    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        String Cmd  = Globals.SWIFT_GET_NODES;
	        String Partition = request.getPartition();
	        String file = request.getFile();
	        Cmd += ("," + "--partition=" + Partition + "," + file);
	        return filt.ExecCmd(Cmd,  Globals.GetSwiftDir(request));
	    }
	    

}