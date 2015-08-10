/**
 * 
 */
package com.seagate.kinetic.tools.external;

/**
 * @author mshafiq
 *
 */
public class Recon implements ExternalCommandService {
	 public Recon() {}
	    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        return filt.ExecCmd(filt.GetReconCommand(request.getResource()),
	        		Globals.GetSwiftDir(request));
	    }

}