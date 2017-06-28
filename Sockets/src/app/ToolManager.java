package app;

import java.io.*;	//For reading command line output
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;

public class ToolManager {
	private int connection_id;
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
		connection_id = lz.getConnID();
		protocol = lz.getType();
		target_id = lz.getTargetID();
		target_server = lz.getTargetServer();
		source_LZ = lz.getSourceLZ();
		target_LZ = lz.getTargetLZ();
	}
   
	public String execute(List<String> commands, String heredoc){
		String output = "";
		ProcessBuilder builder;
		Process proc;
		try {
			builder = new ProcessBuilder(commands);//("/bin/bash", "-c", command);
			builder.redirectErrorStream(true);
			proc = builder.start();
			if (heredoc!=null){
				OutputStream os = proc.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
				writer.write(heredoc);
				writer.flush();
				writer.close();
				os.flush();
				os.close();
			}
			proc.waitFor();
			exitStatus = proc.exitValue(); //will return 0 for normal termination (success)
			output = returnStream(proc.getInputStream());  //suppress this when not testing. this is good for seeing the results of the commands you run
			if (exitStatus == 0 && protocol.contains("ssh")){
				 return "1";
			}
			else if (exitStatus == 0 ){
				 return output;
			}
			else {
				System.out.println("command failed to execute properly");
				return "0";
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "0";
		} catch (InterruptedException e){
			System.out.println(e.getMessage());
			return "0";
		}
	}
	public boolean ping_target(){
		boolean validServer = Ping.ping(target_server, timeout, portNumber);
		return validServer;
	}
	
	//Target Server Connection check via SSH
	public Status_Details targetSSHConnection(){
		Status_Details ssh_status = new Status_Details(connection_id);
		int ssh_success = 0;
		String target = target_id + "@" + target_server;
		
		List<String> commands = new ArrayList<String>(Arrays.asList("ssh", "-o","StrictHostKeyChecking=no","-o","BatchMode=yes",
				target, "pwd"));
	
		ssh_success = Integer.parseInt(execute(commands, null)); //0 or 1
		
		if (ssh_success == 1) {
			ssh_status.setSuccess(1);
			ssh_status.setPerm(1);
			ssh_status.setLZ(1);
		}
	    return ssh_status;
	}

	//creates a dummy file, puts that file on remote over sftp, scans output for failure keywords: "denied" 
	//or "No such" (like augustine's)
	public Status_Details SFTPTransferValidation(){	
		Status_Details sftp_status = new Status_Details(connection_id);
		String testFileName = "sftpTransfer.bat";
		String output="";
		String sourceFilePath;
		String targetFilePath;
		if (source_LZ.equals("/")){
			sourceFilePath = source_LZ + testFileName;
		}
		else { 
			sourceFilePath = source_LZ + "/" + testFileName; 
		}
		
		if (target_LZ.equals("/")){
			targetFilePath= target_LZ  + testFileName;
		}
		else { 
			targetFilePath= target_LZ + "/" + testFileName;
		}
		String dummyText = "this file will be created on the source and copied over sftp to the target"; 
		
		//creates test file on source server
		makeTestFile(sourceFilePath, dummyText);
		
		String command = "sftp";
		if (protocol.equalsIgnoreCase("sftp2")){
			command = "/opt/tectia/bin/sftp2";
		}
		String target = target_id + "@" + target_server;
		String heredoc = "put " + sourceFilePath + " " + targetFilePath;
		List<String> sftpCommand = new ArrayList<String>(Arrays.asList(command, "-o","StrictHostKeyChecking=no","-o","BatchMode=yes",
				target));
		output = execute(sftpCommand, heredoc); 		
		//System.out.println(output);
		
		//case in which file upload fails but output doesn't contain "denied", wasn't a permission error, so perm=1
		if (!output.contains("denied")){
			sftp_status.setPerm(1);
		}
		//case in which file upload fails but output doesn't contain "No such", wasn't a lz error, so lz=1
		if (!output.contains("No such")){
			sftp_status.setLZ(1);
		}
		if (output.contains("Uploading") && !output.contains("No such") &&!output.contains("denied")){
			sftp_status.setSuccess(1);
		}
		removeTestFile(sourceFilePath, targetFilePath, sftpCommand);
		return sftp_status;
	}
	public void makeTestFile(String targetFileLocation, String internalCommand) {
		try {
			PrintWriter writer = new PrintWriter(targetFileLocation);
			writer.println(internalCommand);
			writer.close();
		}
		catch (IOException e ){
			System.out.println(e.getMessage());
		}
		//System.out.println("Dummy File Created");
	}
	public void removeTestFile(String sourceFileLocation, String targetFileLocation, List<String> sftpCommand){
		List<String> remove = new ArrayList<String>(Arrays.asList("rm", sourceFileLocation));
		execute(remove, null);
		
		String heredoc = " rm " + targetFileLocation ;
		execute(sftpCommand, heredoc);
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
		} catch (IOException e){
			System.out.println(e.getMessage());  
		}
		return output.toString();
	}
	
}
