package app;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ConfigRepository.Config;

import java.io.InputStream;	//For reading command line output
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;

public class ToolManager {
	private String target_id;
	private String target_server; //target server to be SSH'd to for initialization of connection check

	
	// Default values
	private int portNumber;
	private int timeout;
	private int exitStatus;	
	
	public void commonConstructor(){
		portNumber = 22;
		timeout = 3000;
		exitStatus = 0;
	}
	public ToolManager(String target_id, String target_server){
		commonConstructor();
		this.target_id = target_id;
		this.target_server = target_server;
	}

// For ToolManager to talk to ThreadManager to create a thread for the upcoming command execution
	public boolean executeCommand(String command) {
		try {
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			exitStatus = p.exitValue(); //will return 0 for normal termination (success)
			printStream(p.getInputStream(), "OUTPUT");
			printStream(p.getErrorStream(), "ERROR");
			
			if (exitStatus == 0 ){
				 return true;
			}
			else {
				System.out.println("command failed");
				return false;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	/*public boolean execute(String command){
		try {
			ChannelExec channelexec = (ChannelExec)session.openChannel("exec"); //will run your ssh or sftp command on the target
			channelexec.setCommand(command);
			channelexec.connect();
			//run loop until command finished execution, then get its exit status
			while (true) {
				exitStatus = channelexec.getExitStatus();
				if (exitStatus > -1){
					break;
				}
			}
			channelexec.disconnect();
			if (exitStatus == 0){
				return true;
			}
			else {
				System.out.println ("command " +command+ " failed: " + exitStatus);
				return false;
			}
		} catch (JSchException e){
			//left out connection message
			System.out.println("exception caught");
			return false;
		}
	}
	*/
	public int ping_target(){
		int statusCode = Ping.ping(target_server, timeout, portNumber);
		return statusCode;
	}
	
	public int targetServerConnection(String protocol){
		int statusCode = 0;
		boolean targetServerConnected = false;
		if (protocol.equalsIgnoreCase("ssh")){
			targetServerConnected = targetSSHConnection();
			if(!targetServerConnected){
				statusCode = 311;
			}
		}
		else if(protocol.equalsIgnoreCase("sftp")) {
			targetServerConnected = targetSFTPConnection();
			if(!targetServerConnected) { //failed sftp connection
				statusCode = 312;
			}
		}
		//else if -- handle other protocols. 
		return statusCode;
	}
	
	//Target Server COnnection check via SSH
	public boolean targetSSHConnection(){
		boolean targetSSHConnected = false;
		String command = "ssh " + target_id + "@" + target_server + " cp /home/vivian/Desktop/asdgsxcv/new.txt nnnew ";
		System.out.println("ssh command" + command);
		targetSSHConnected = executeCommand(command);
		return targetSSHConnected;
	}
	
	//Target Server Connection check via SFTP
	public boolean targetSFTPConnection() {
		boolean targetSFTPConnected = false;
		String home = System.getProperty("user.home"); //instead of $HOME, which doesn't seem
		//to evaluate correctly when executed by runtime. 
		
		String filename = "test.bat";
		String targetFilePath = home +"/"+ filename;
		String targetServerName = target_id + "@" + target_server;
		String command = "ls";
		makeTestFile(targetFilePath, command);
		
		command = "sftp -b " + targetFilePath + " " + targetServerName;  
		System.out.println("TargetSFTPConnection:" + command);
		targetSFTPConnected = executeCommand(command);
		
		Path filepath = Paths.get(targetFilePath);
		try {
			Files.delete(filepath);
		} catch(Exception x) {
			System.out.println("dummy file wasn't deleted");
		}
		
		return targetSFTPConnected;
	} 
	
	public void makeTestFile(String targetFileLocation, String internalCommand) {
		try {
			PrintWriter writer = new PrintWriter(targetFileLocation);
			writer.println(internalCommand);
			writer.close();
		}
		catch (IOException e ){
			e.printStackTrace();
		}
		System.out.println("Dummy File Created");
	}
	
	public static void printStream(InputStream is, String type){
		try(InputStreamReader isr = new InputStreamReader(is);
		      BufferedReader br = new BufferedReader(isr)) 
			{
				String line = null;
			    while ( (line = br.readLine()) != null)
			            System.out.println(type + ">" + line);    
			} catch (IOException ioe){
		           ioe.printStackTrace();  
			}
	}
	
}
