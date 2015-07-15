package com.seagate.kinetic.tools.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;



public class CommandFilter {
	
	private CommandFilter() 
	{
		// Update the configuration filter
		ConfigMap.put("proxy", "swift-config,proxy");
		ConfigMap.put("account", "swift-config,account");
		ConfigMap.put("object", "swift-config,object");
		ConfigMap.put("container", "swift-config,container");
		
		RingMap.put("proxy", "swift-ring-builder,proxy.builder");
		RingMap.put("object", "swift-ring-builder,object.builder");
		RingMap.put("account", "swift-ring-builder,account.builder");
		RingMap.put("conatiner", "swift-ring-builder,container.builder");
		
		InitMap.put("proxy", "swift-init,proxy,status");
		InitMap.put("object", "swift-init,object,status");
		InitMap.put("account", "swift-init,account,status");
		InitMap.put("container", "swift-init,container,status");
		
		ReconMap.put("proxy", "swift-recon,proxy,--all");
		ReconMap.put("object", "swift-recon,object,--all");
		ReconMap.put("account", "swift-recon,account,--all");
		ReconMap.put("container", "swift-recon,container,--all");
		
		DispersionMap.put("all", "swift-dispersion-report,-d,-j");
		
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
	public ExternalResponse ExecCmd(String cmd, String dir)
	{
		String rc;
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
				//proc.wait(60000);
				BufferedReader reader = 
                        new BufferedReader(new InputStreamReader(proc.getInputStream()));	
				while ((line = reader.readLine())!= null) {
					
					output.append(line + "\n");
					}
				System.out.println("Sending Response " + output.toString());
				
				rc =  output.toString();
			} catch (Exception e) {
				e.printStackTrace();
				rc =  "Internal Error";
			}
		}
		else
			rc =  "Invalid Request";
		ExternalResponse resp = new ExternalResponse();
		resp.setResponseMessage(rc);
	    return resp;
	}
	private  HashMap<String, String> ConfigMap = new HashMap<String, String>();
	private  HashMap<String, String> RingMap = new HashMap<String, String>();
	private  HashMap<String, String> InitMap = new HashMap<String, String>();
	private  HashMap<String, String> ReconMap = new HashMap<String, String>();
	private  HashMap<String, String> DispersionMap = new HashMap<String, String>();

}
