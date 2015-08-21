package com.seagate.kinetic.tools.external.swift;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;

public class Info implements ExternalCommandService {
	public static final Logger logger = Logger.getLogger(Info.class.getName());
	public Info() {}
    @Override
    public ExternalResponse execute(ExternalRequest req) {

        SwiftRequest request = (SwiftRequest) req;

        System.out.println("** received request: " + request.toJson());
        CommandFilter filt = CommandFilter.getInstance();
        String dir = request.getDir();
        String file = request.getFile();
        String msg = request.getResource();
        String cmd = filt.GetInfoCommand(msg);
        String result = new String();
        if (dir == null ) dir = Globals.SWIFT_DATA_DIR;
        if (file == null) {
        	File dirName = new File(dir); 
        	List<File> list = FilesList(null, dirName);
        	// Create list of file
        	for (File item : list) {
        		String name = item.getName();
        		String path = item.getAbsolutePath();
        		if (path.contains(msg)) {
        			try {
        				String extension = name.substring(name.lastIndexOf("."));
        				if (extension.equalsIgnoreCase(Globals.SWIFT_DB_EXTENSION)) {
        					result += (path + "\n");
        				}

        	    		} catch (Exception e) {
        	    		logger.info("no extension founds for File " + name);
        	    		}
        		}
        	}
        	logger.info("Returning list " + result);
        		
        }
        else {
        	cmd += ("," + file);        	
        	logger.info("executing Command " + cmd + " in Directory == " + dir + " for File == " + file );
        	result = filt.ExecShellCmd(cmd, dir);
        }
        ExternalResponse resp = new ExternalResponse();
		resp.setResponseMessage(result);
        return resp;
        
    }
    private List<File> FilesList(List<File> files, File dirFile)
    {
        if (files == null)
            files = new LinkedList<File>();
        if (!dirFile.isDirectory())
        {
            files.add(dirFile);
            return files;
        }
        for (File file : dirFile.listFiles())
            FilesList(files, file);
        return files;
    }
    
}