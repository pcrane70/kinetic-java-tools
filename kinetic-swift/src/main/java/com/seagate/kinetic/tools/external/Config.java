/**
 * 
 */
package com.seagate.kinetic.tools.external;


/**
 * @author mshafiq
 *
 */
public class Config implements ExternalCommandService {
	 public Config() {}

    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

        System.out.println("** received request: " + request.toJson());
        CommandFilter filt = CommandFilter.getInstance();
        return filt.ExecCmd(filt.GetConfigCommand(request.getResource()),
                Globals.GetSwiftDir(request));
    }

}
