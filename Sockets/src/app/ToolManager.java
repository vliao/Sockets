package app;

import java.io.InputStream;	//For reading command line output
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
	
	public ToolManager(LZ_Details lz){
		portNumber = 22;
		timeout = 3000;
		exitStatus = 0;
		protocol = lz.getType();
		target_id = lz.getTargetID();
		target_server = lz.getTargetServer();
		source_LZ = lz.getSourceLZ();
		target_LZ = lz.getTargetLZ();
	}
   
	public String execute(String command){
		String output = "";
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.waitFor();
			exitStatus = p.exitValue(); //will return 0 for normal termination (success)
			output = returnStream(p.getInputStream());  //suppress this when not testing. this is good for seeing the results of the commands you run
			
			if (exitStatus == 0 ){
				 return output;
			}
			else {
				System.out.println("command failed");
				return "false";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "false";
		} catch (InterruptedException e){
			e.printStackTrace();
			return "false";
		}
	}
	public boolean ping_target(){
		boolean validServer = Ping.ping(target_server, timeout, portNumber);
		return validServer;
	}
	
	//Target Server Connection check via SSH
	public boolean[] targetSSHConnection(){
		String output="";
		boolean[] result = new boolean[3];
		String command = null;
		String options = " -o StrictHostKeyChecking=no -o BatchMode=yes ";
		if (protocol.equalsIgnoreCase("ssh")){
			command =  "ssh"+ options  + target_id + "@" + target_server + " pwd ";
		}
		else { command = "/opt/tectia/bin/ssh2" + options + target_id + "@" + target_server + " pwd ";} //not tested, copied from david. 
		System.out.println("ssh command: " + command);
		output = execute(command);
		
		if(!output.contains("Permission denied")){
			result[0]=true;
			result[1]=true;
			result[2]=true;
		}
	    return result;
	}

	//creates a dummy file, puts that file on remote over sftp, scans output for failure keywords: "denied" 
	//or "No such" (like augustine's)
	public boolean[] SFTPTransferValidation(){	
		String testFileName = "sftpTransfer.bat";
		String output;
		String options = "-o StrictHostKeyChecking=no -o BatchMode=yes ";
		String command = "sftp ";
		if (protocol.equalsIgnoreCase("sftp2")){
			command = "/opt/tectia/bin/sftp2 ";
		}
		
		boolean sftpTransfer= true;
		boolean permission = true;
		boolean validLZ = true;
		String sourceFilePath = source_LZ + "/" + testFileName;
		String targetFilePath = target_LZ + "/" + testFileName;
		String dummyText = "this file will be created on the source and put on target by sftp"; 
		makeTestFile(sourceFilePath, dummyText);
		
		
		String sftpCommand = command + options + target_id + "@" + target_server + " <<<  \"put " 
				+ sourceFilePath + " " + targetFilePath + "\" ";
		output = execute(sftpCommand); 		
		//System.out.println(output);
		
		if (output.contains("No route") || output.equals("false")){ //no access to server- lacks ssh keys
			permission = false;
			validLZ= false;
			sftpTransfer = false;
		}
		if (output.contains("denied")){ //no permission to copy files to the LZ
			permission= false;
			sftpTransfer = false;
		}
		if (output.contains("No such")){//invalid LZ or something went wrong when creating the dummy file
			validLZ= false;
			sftpTransfer = false;
		}
		
		boolean[] result = new boolean[3];
		result[0] = sftpTransfer;
		result[1] = permission;
		result[2] = validLZ;
		
		removeTestFile(sourceFilePath, targetFilePath, command);
		return result;
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
		//System.out.println("Dummy File Created");
	}
	public void removeTestFile(String sourceFileLocation, String targetFileLocation, String command){
		String remove = " rm \""+ sourceFileLocation + "\"";
		execute(remove);
		remove = command + target_id + "@" + target_server + " <<<  \" rm " + targetFileLocation + "\"";
		execute(remove);
	}
	public String returnStream(InputStream is){
		StringBuilder output = new StringBuilder();
		try{
		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
			try{ 
			    String line = null;
				while ( (line = br.readLine()) != null){
			        //System.out.println("process output: >" + line);    
					output.append(line);}
			} finally {
				br.close();
			}
		} catch (IOException ioe){
	           ioe.printStackTrace();  
		}
		return output.toString();
	}
	
}
