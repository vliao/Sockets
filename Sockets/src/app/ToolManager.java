package app;

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
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.waitFor();
			exitStatus = p.exitValue(); //will return 0 for normal termination (success)
			printStream(p.getInputStream(), "OUTPUT");
			
			if (exitStatus == 0 ){
				 return true;
			}
			else {
				System.out.println("command failed");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e){
			e.printStackTrace();
			return false;
		}
		
	}
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
		String command = " ssh -o \"StrictHostKeyChecking no \" " + target_id + "@" + target_server + " cp /home/vivian/Desktop/asdgsxcv/new.txt nnnew ";
		System.out.println("ssh command: " + command);
		targetSSHConnected = executeCommand(command);
		return targetSSHConnected;
	}
	
	//Target Server Connection check via SFTP
	//create a dummy file with a simple command in source home, run it over sftp, then delete dummy file. 
	public boolean targetSFTPConnection() {
		boolean targetSFTPConnected = false;
		String home = System.getProperty("user.home");  
		
		String filename = "test.bat";
		String targetFilePath = home +"/"+ filename;
		String targetServerName = target_id + "@" + target_server;
		String command = "pwd";
		makeTestFile(targetFilePath, command);
		
		command = "sftp -o \"StrictHostKeyChecking no \" -b  " + targetFilePath + " " + targetServerName;  
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
	
	public int fileTransferValidation (String protocol, String source_LZ, String target_LZ){
		int statusCode = 0;
		boolean fileTransferValidation = false;
		if(protocol.equalsIgnoreCase("sftp")){
			fileTransferValidation = SFTPTransferValidation(source_LZ, target_LZ);
			if (!fileTransferValidation){
				statusCode = 502;
			}
		}
		return statusCode;
		
	}
	
	public boolean SFTPTransferValidation(String source_LZ, String target_LZ){
		String batchFileName = "sftpTransfer.bat";
		//String batchRemovalFileName = "targetLZFilRemovalTest.bat";
		String sftpCommand = "";
		boolean sftpTransferValidated = false;
		
	//	can't be $HOME, needs to be HOME for this to work. 
		// sftp is a separate program than bash, can't use env. variables like $HOME
		String source_LZ1 = System.getenv(source_LZ);
		String target_LZ1 = System.getenv(target_LZ);
		
		String sourceFilePath = source_LZ1 + "/" + batchFileName;
		String transferCommand = "put " + sourceFilePath + " "+ target_LZ1 ; //like scp, puts file on remote. 
		System.out.println(transferCommand);
		makeTestFile(sourceFilePath, transferCommand);
		
		sftpCommand = "sftp -b \"" + sourceFilePath + "\" " + target_id + "@" + target_server;
		sftpTransferValidated = executeCommand(sftpCommand); //fails if transfer fails
		System.out.println("transfervalidation? " + sftpTransferValidated);
		return sftpTransferValidated; 
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
		try{
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
			try{ 
			    String line = null;
				while ( (line = br.readLine()) != null)
			            System.out.println(type + ">" + line);    
			} finally {
				br.close();
			}
		} catch (IOException ioe){
	           ioe.printStackTrace();  
		}
	}
	
}
