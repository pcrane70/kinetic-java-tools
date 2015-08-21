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
public class Init implements ExternalCommandService {
	 public Init() {}
	    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        return filt.ExecCmd(filt.GetInitCommand(request.getResource()),
	        		Globals.GetSwiftDir(request));
	    }
}

