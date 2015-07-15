/**
 * 
 */
package com.seagate.kinetic.tools.external;

import java.util.logging.Logger;

/**
 * @author mshafiq
 *
 */
public class Config implements ExternalCommandService {
	 public Config() {}
	    @Override
	    public ExternalResponse execute(ExternalRequest request) {
	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        return filt.ExecCmd(filt.GetConfigCommand(request.getRequestMessage()), null);
	    }

}
