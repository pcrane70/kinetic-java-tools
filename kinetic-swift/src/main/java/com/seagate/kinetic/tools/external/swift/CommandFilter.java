package com.seagate.kinetic.tools.external.swift;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Logger;

import com.seagate.kinetic.tools.external.ExternalResponse;

public class CommandFilter {
	public static final Logger logger = Logger.getLogger(CommandFilter.class.getName());
	
	private static  void UpdateSwiftDir()
	{
		String value = System.getenv(Globals.SWIFT_ENV_DIR);
		if (value != null) Globals.SWIFT_DIR = value;
		logger.info("Using swift dir....." + Globals.SWIFT_DIR);
	}
	
	private CommandFilter() 
	{
		UpdateSwiftDir();
		// Update the configuration filter
		ConfigMap.put("proxy", "swift-config,proxy");
		ConfigMap.put("account", "swift-config,account");
		ConfigMap.put("object", "swift-config,object");
		ConfigMap.put("container", "swift-config,container");
		
		RingMap.put("proxy", "swift-ring-builder");
		RingMap.put("object", "swift-ring-builder");
		RingMap.put("account", "swift-ring-builder");
		RingMap.put("container", "swift-ring-builder");
		
		InitMap.put("proxy", "swift-init,proxy,status");
		InitMap.put("object", "swift-init,object,status");
		InitMap.put("account", "swift-init,account,status");
		InitMap.put("container", "swift-init,container,status");
		
		ReconMap.put("proxy", "swift-recon,proxy,--all");
		ReconMap.put("object", "swift-recon,object,--all");
		ReconMap.put("account", "swift-recon,account,--all");
		ReconMap.put("container", "swift-recon,container,--all");
		
		DispersionMap.put("populate", "swift-dispersion-populate");
		DispersionMap.put("report", "swift-dispersion-report,-d,-j");
		
		PartitionMap.put("object", "swift-ring-builder");
		
		InfoMap.put("object", "swift-object-info");
		InfoMap.put("account", "swift-account-info");
		InfoMap.put("container", "swift-container-info");
		 
	}
	
	private static class Holder 
	{
		public static final CommandFilter instance = new CommandFilter();
	}
	
	public static CommandFilter getInstance() 
	{
		return Holder.instance;
	}
	public String GetConfigCommand(String req)
	{
		return ConfigMap.get(req);
		
	}
	public String GetRingCommand(String req)
	{
		return RingMap.get(req);
		
	}
	public String GetInitCommand(String req)
	{
		return InitMap.get(req);
		
	}
	public String GetReconCommand(String req)
	{
		return ReconMap.get(req);
		
	}
	public String GetDispersionCommand(String req)
	{
		return DispersionMap.get(req);
		
	}
	public String GetPartitionCommand(String req)
	{
		return PartitionMap.get(req);
		
	}
	public String GetInfoCommand(String req)
	{
		return InfoMap.get(req);
		
	}
	public String ExecShellCmd(String cmd, String dir)
	{
		String rc;
		logger.info("Executing Command " + cmd);
		if (cmd != null) {
			ProcessBuilder builder;
			StringBuffer output = new StringBuffer();
			String line = "";
			try {
				String[] args = cmd.split(",");
				
				builder = new ProcessBuilder(args);
				if (dir != null)
					builder.directory(new File(dir));
				
				Process  proc = builder.start();
				
				//proc.wait(6000);
				BufferedReader Inputreader = 
                        new BufferedReader(new InputStreamReader(proc.getInputStream()));
				BufferedReader Errorreader = 
                        new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				
				while ((line = Inputreader.readLine())!= null) {
					
					output.append(line + "\n");
					}
				while ((line = Errorreader.readLine())!= null) {
					
					output.append(line + "\n");
					}
				
				logger.info("Sending Response " + output.toString());
				
				rc =  output.toString();
			} catch (Exception e) {
				e.printStackTrace();
				rc =  "Internal Error";
			}
		}
		else
			rc =  "Invalid Request";
		
		return rc;
	}
	public ExternalResponse ExecCmd(String cmd, String dir)
	{
		
		ExternalResponse resp = new ExternalResponse();
		resp.setResponseMessage(ExecShellCmd(cmd, dir));
	    return resp;
	}
	private  HashMap<String, String> ConfigMap = new HashMap<String, String>();
	private  HashMap<String, String> RingMap = new HashMap<String, String>();
	private  HashMap<String, String> InitMap = new HashMap<String, String>();
	private  HashMap<String, String> ReconMap = new HashMap<String, String>();
	private  HashMap<String, String> DispersionMap = new HashMap<String, String>();
	private  HashMap<String, String> PartitionMap = new HashMap<String, String>();
	private  HashMap<String, String> InfoMap = new HashMap<String, String>();

}
