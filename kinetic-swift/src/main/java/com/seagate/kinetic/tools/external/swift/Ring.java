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
public class Ring implements ExternalCommandService {
	 		public Ring() {}
	   	    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

	   	        System.out.println("** received request: " + request.toJson());
	   	        CommandFilter filt = CommandFilter.getInstance();
	   	        String Cmd  = filt.GetRingCommand(request.getResource());
	   	        String file = request.getFile();
	   	        if (file == null) file = Globals.GetSwiftRingFile(request);
	   	        Cmd += ("," + file);
		        return filt.ExecCmd(Cmd, 
		        		Globals.GetSwiftDir(request));
	   	    }
	   	    

}
