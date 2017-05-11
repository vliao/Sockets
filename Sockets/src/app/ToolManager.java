package app;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ConfigRepository.Config;

import java.io.InputStream;	//For reading command line output
import java.io.IOException;

public class ToolManager {
	private JSch ssh_object;
	private String source_id;
	private String source_server; //Source server to be SSH'd to for initialization of connection check
	private Session session;
	private ConfigRepository configRepository;
	
	// Default values
	private String privatekey;
	private int portNumber;
	private int timeout;
	private int exitStatus;	
	
	public void commonConstructor(){
		ssh_object = new JSch();
		session = null;
		
	/*	try{
			configRepository = com.jcraft.jsch.OpenSSHConfig.parseFile("~/.ssh/config");
		} catch (IOException e){
			e.printStackTrace();
		}
		*/
		portNumber = 22;
		timeout = 3000;
		exitStatus = 0;
	}
	public ToolManager(String source_id, String source_server){
		commonConstructor();
		this.source_id = source_id;
		this.source_server = source_server;
	}
	
	public int ping_target(){
		int statusCode = Ping.ping(source_server, timeout, portNumber);
		return statusCode;
	}
}
