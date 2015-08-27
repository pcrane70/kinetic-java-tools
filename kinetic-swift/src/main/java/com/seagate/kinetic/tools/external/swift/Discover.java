/**
 * 
 */
package com.seagate.kinetic.tools.external.swift;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.seagate.kinetic.tools.external.ExternalCommandService;
import com.seagate.kinetic.tools.external.ExternalRequest;
import com.seagate.kinetic.tools.external.ExternalResponse;


/**
 * @author mshafiq
 *
 */
public class Discover implements ExternalCommandService {
	public static final Logger logger = Logger.getLogger(Discover.class.getName());
		public  Discover() {}
	    @Override
    public ExternalResponse execute(ExternalRequest req) {
	    	ExternalResponse resp = new ExternalResponse();
	    	SwiftRequest request = (SwiftRequest) req;
	    	List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	        System.out.println("** received request: " + request.toJson());
	        dir = request.getDir();
	        user = request.getUser();
	        password = request.getPassword();
	        host = request.getHost();
	        if (dir == null ) dir = Globals.IPMI_DIR;
	        if (user == null)  user = Globals.IPMI_USER;
	        if (password == null)  password = Globals.IPMI_PASSWORD;
	        if (host == null) host=Globals.IPMI_HOST;
	        List<InetAddress> addrs = GetIPMIControllers();
	        for (InetAddress addr : addrs) {
	        	Map<String, String> map = ChassisInfo(addr.getHostAddress());
	        	if (map != null && !map.isEmpty()) {
	        		map.put("IPAddrss", addr.getHostAddress());
	        		list.add(map);
	        	}
	        }
	        Gson gson = new Gson();
			resp.setResponseMessage(gson.toJson(list));
			logger.info("Sending response " + gson.toJson(list));
	        return resp;
	        
	    }
	    private Map<String, String> ChassisInfo(String host)
	    {	
	    	 CommandFilter filt = CommandFilter.getInstance();
	    	String cmd = "ipmitool" + "," + "-Ilan"+  "," + "-U" + user + "," + "-P" + password +
	        		"," +"-H" + host + "," + "fru,print";
	    	String rc  = filt.ExecShellCmd(cmd, dir);
	    	logger.info("Reported IPMI Chassis " + rc);
	    	String rcLower = rc.toLowerCase();
	    	Map<String, String> map = new HashMap<String, String>();
	    	if (rcLower.contains(Globals.IPMI_SUPERMICRO_TAG)) /* && rcLower.contains(Globals.IPMI_SUPERMICRO_PART)) */{
	    		
	    		StringTokenizer st = new StringTokenizer(rc, "\n");
	    		logger.info("Tokenized String:" + rc + " Number of Tokens " + st.countTokens());
		    	while (st.hasMoreTokens()) {
		    		String line = st.nextToken().trim();
		    		String[] tokens = line.split(":");
		    		String key = tokens[0].trim();
		    		if (key != null) {
		    			String val = "";
		    			if (tokens.length >= 2 &&  tokens[1] != null) val = tokens[1];
		    			logger.info("Adding Chassis Key: " + key + " val " + val);
		    			map.put(key, val.trim());
		    		}
		    	}
	    	}
	    	Gson gson =  new Gson();
	    	logger.info("Chassis Map: " + gson.toJson(map));
	    	return map;
	    }
	    class RcvThread extends Thread {
	    	public RcvThread(DatagramSocket sock) { socket = sock; } 
	    	public void run()
	    	{
	    		RcvPingResponse();
	    	}
	    	public DatagramSocket getSocket()
	    	{
	    		return socket;
	    	}
	    	public List<InetAddress> getChassisAddrs()
	    	{
	    		work = false;
	    		try {
	    			Thread.sleep(Globals.IPMI_PING_TIMEOUT);
	    		}catch (Exception e){
	    			logger.info("Failed to stop rcv thread" + e.toString());
	    		}
	    		Gson gson =  new Gson();
		    	logger.info("Chassis Addrs: " + gson.toJson(ChassisAddrs));
	    		return ChassisAddrs;
	    	}
	    	private void RcvPingResponse()
	    	{
	    		try {
	    			
	    			byte[] receiveData = new byte[Globals.IPMI_MAX_DATA];
	    			while(work){
		            	try {
		                  DatagramPacket receivePacket = new DatagramPacket(receiveData, 
		                		  receiveData.length);
		                  socket.receive(receivePacket);
		                  ChassisAddrs.add(receivePacket.getAddress());
		                  logger.info("Ping Response Received: " + receivePacket.getAddress().getHostAddress());
		            	}catch (Exception e){
		            		logger.info("Retrying to read IPMI Ping response" + e.toString());
				        }
	    			}
	    		}catch (Exception e){
		        	logger.info("Failed create Rcv Socket" + e.toString());
		        }
		        
	    	}
	    	List<InetAddress> ChassisAddrs = new ArrayList<InetAddress>();
	    	boolean work = true;
	    	DatagramSocket socket;
	    }
	    
	    private List<InetAddress> SendPingRequest(List<InetAddress[]> list)
	    {
	    	final byte[] ping = new byte[]{0x06, 0x00, (byte)0xff, 0x06, 0x00, 0x00, 0x11, (byte)0xbe, (byte) 0x80, 0x00, 0x00, 0x00};
	    	List<InetAddress> sources = new ArrayList<InetAddress>();
	    	List<RcvThread> threads = new ArrayList<RcvThread>();
	    	for (InetAddress addr[] : list) {
	    		try {
	    			InetSocketAddress address = new InetSocketAddress(addr[1].getHostAddress(), port);
	    			DatagramPacket packet = new DatagramPacket(ping, ping.length, addr[0], Globals.IPMI_PORT);
	    			DatagramSocket socket = new DatagramSocket(address);
	    			socket.setSoTimeout(Globals.IPMI_PING_TIMEOUT);	
	    	        
	    			RcvThread rcv = new RcvThread(socket);
	    			rcv.start();
	    			socket.send(packet);
	    			threads.add(rcv);
	    			logger.info("Ping request sent to " + addr[0].getHostAddress() +  socket.getPort() + " from " + 
	    			addr[1].getHostAddress() + ":" + port);
	    		}
	    		catch (Exception e) {
	    			logger.info("Ping request cannot be sent to " + addr[0].getHostAddress() + " from " + addr[1].getHostAddress() + 
	    					":" + port  + e.toString());
	    			
	    		}
	    		
	    		
	    	}
	    	for (RcvThread th: threads) {
	    		DatagramSocket socket = th.getSocket();
	    		sources.addAll(th.getChassisAddrs());
	    		socket.close();
	    	}
	    	Gson gson =  new Gson();
	    	logger.info("List of IPMI Sources: " + gson.toJson(sources));
	    	return sources;
	    	
	    }
	    private List<InetAddress> GetIPMIControllers()
	    {
	    	List<InetAddress[]> list = GetAddresses();
	    	return SendPingRequest(list);
	    }
	    
	    private List<InetAddress[]> GetAddresses()
	    {
	    	List<InetAddress[]> list = new ArrayList<InetAddress[]>();
	        try {
	        	Enumeration<NetworkInterface> intfs = NetworkInterface.getNetworkInterfaces();
	        	while(intfs.hasMoreElements()) {
	                    NetworkInterface intf = (NetworkInterface) intfs.nextElement();
	                    if(intf == null) continue;
	                    if(!intf.isLoopback() && intf.isUp()) {
	                        Iterator<InterfaceAddress> it = intf.getInterfaceAddresses().iterator();
	                        while (it.hasNext()) {
	                            InterfaceAddress IntfAddr = (InterfaceAddress) it.next();
	                            if(IntfAddr == null) continue;
	                            InetAddress[] addr = new InetAddress[2];
	                            addr[0] = IntfAddr.getBroadcast();
	                            addr[1] = IntfAddr.getAddress();
	                            if (addr[0] != null && addr[1] != null) 
	                            	list.add(addr);
	                        }
	                    }
	                }
	            } catch (SocketException e) {
	                logger.info(e.toString());
	                list = null;
	            }
	        Gson gson =  new Gson();
	    	logger.info("Discovered Interfaces with Broadcast and IP addresses: " + gson.toJson(list));
	        return list;
	    }     
	    String user = null;
	    String password = null;
	    String dir = null;
	    String host = null;
	    int	port = Globals.IPMI_UDP_PORT;  
}