package com.seagate.kinetic.tools.external;

public class Swift implements ExternalCommandService {
		public Swift() {}
	    @Override
	    public ExternalResponse execute(ExternalRequest req) {
	    	SwiftRequest request = (SwiftRequest) req;
	    	System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        String Cmd  = Globals.SWIFT_COMMAND;
	        String url = request.getUrl();
	        String user = request.getUser();
	        String key = request.getKey();
	        String resource = request.getResource();
	        String command = request.getCommand();
	        Cmd +=("," + "-A" + url + "," + "-U" + user +"," + "-K" + key + "," + command + "," + resource); 
	        return filt.ExecCmd(Cmd,  Globals.GetSwiftDir(request));
	    }
	    

}
