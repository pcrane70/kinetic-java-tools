/**
 * 
 */
package com.seagate.kinetic.tools.external.swift;

import java.util.logging.Logger;

import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;

/**
 * @author mshafiq
 *
 */
public class Dispersion implements ExternalCommandService {
	public static final Logger logger = Logger.getLogger(Dispersion.class.getName());
		public Dispersion() {}

    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

        System.out.println("** received request: " + request.toJson());
        CommandFilter filt = CommandFilter.getInstance();
        logger.info("For Dispersion using Swift Dir Path...."
                + Globals.SWIFT_DIR);
        return filt.ExecCmd(filt.GetDispersionCommand(request.getResource()),
                Globals.GetSwiftDir(request));
    }
	    

}

