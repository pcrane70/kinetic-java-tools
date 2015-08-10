/**
 * 
 */
package com.seagate.kinetic.tools.external;

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

