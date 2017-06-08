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
	private String protocol;
	private String target_id;
	private String target_server; //target server to be SSH'd to for initialization of connection check
	private String source_LZ;
	private String target_LZ;
	
	// Default values
	private int portNumber;
	private int timeout;
	private int exitStatus;	
	
	public void commonConstructor(){
		portNumber = 22;
		timeout = 3000;
		exitStatus = 0;
	}
	public ToolManager(String protocol, String source_LZ, String target_LZ, String target_id, String target_server){
		commonConstructor();
		this.protocol = protocol;
		this.target_id = target_id;
		this.target_server = target_server;
		this.source_LZ = source_LZ;
		this.target_LZ = target_LZ;
	}
   
	public boolean executeCommand(String command) {
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.waitFor();
			exitStatus = p.exitValue(); //will return 0 for normal termination (success)
			printStream(p.getInputStream(), "Process OUTPUT");  //suppress this when not testing. this is good for seeing the results of the commands you run
			
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
	
	public boolean targetServerConnection(){
		boolean targetServerConnected = false;
		if (protocol.equalsIgnoreCase("ssh")){
			targetServerConnected = targetSSHConnection();
		}
		else if(protocol.equalsIgnoreCase("sftp")) {
			targetServerConnected = targetSFTPConnection();
		}
		//else if -- handle other protocols. 
		return targetServerConnected;
	}
	
	//Target Server Connection check via SSH
	public boolean targetSSHConnection(){
		boolean targetSSHConnected = false;
		String command =  "ssh -o \"StrictHostKeyChecking no \" " + target_id + "@" + target_server + " pwd ";
		System.out.println("ssh command: " + command);
		targetSSHConnected = executeCommand(command);
		return targetSSHConnected;
	}
	
	//Target Server Connection check via SFTP
	public boolean targetSFTPConnection() {
		boolean targetSFTPConnected = false;
		String command = "sftp -o \"StrictHostKeyChecking no \" -b  - "+ target_id + "@" + target_server + " <<< pwd ";  
		System.out.println("TargetSFTPConnection:" + command);
		targetSFTPConnected = executeCommand(command);
		return targetSFTPConnected;
	} 
	
	public boolean targetLZValidation(){ //if setTargetLZ fails or ls the contents of LZ,
		boolean LZValid = false;
		String command = "sftp -b - " + target_id + "@" + target_server + " <<< \"ls " + target_LZ + "\" ";   
		System.out.println(command);
		LZValid = executeCommand(command);
		return LZValid;
	}

	/*this creates a file on source containing file transfer commands, executes this file on the source, copying this file to target. */
	//fails for invalid target LZ, or no permission to LZ(tested on centos) 
	public boolean SFTPTransferValidation(){	
		String batchFileName = "sftpTransfer.bat";
		boolean sftpTransferValidated = false;
		String sourceFilePath = source_LZ + "/" + batchFileName;
		String transferCommand = "put " + sourceFilePath + " "+ target_LZ ; //like scp, puts file on remote. 
		makeTestFile(sourceFilePath, transferCommand);
		String sftpCommand = "sftp -b \"" + sourceFilePath + "\" " + target_id + "@" + target_server;
		sftpTransferValidated = executeCommand(sftpCommand); //execute the command in the .bat file, fails if transfer fails - due to LZ or permissions.
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
