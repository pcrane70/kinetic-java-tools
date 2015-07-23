/**
 * 
 */
package com.seagate.kinetic.tools.external;



/**
 * @author mshafiq
 *
 */
public class Ring implements ExternalCommandService {
	 		public Ring() {}
	   	    @Override
	   	    public ExternalResponse execute(ExternalRequest request) {
	   	        System.out.println("** received request: " + request.toJson());
	   	        CommandFilter filt = CommandFilter.getInstance();
		        return filt.ExecCmd(filt.GetRingCommand(request.getRequestMessage()), 
		        		Globals.GetSwiftDir(request));
	   	    }
	   	    

}
