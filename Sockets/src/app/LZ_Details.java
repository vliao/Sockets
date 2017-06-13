package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

/* contains parameters gathered from the database
 * used to execute SFTP/LZ Connection check.
 */


public class LZ_Details {
	private String Source_ID;
	private String Source_Server;
	private String Target_ID;
	private String Target_Server;
	private String Type;
	private String Source_LZ;
	private String Target_LZ;
	private String Description;
	private int Connection_ID;
	
	public LZ_Details(ResultSet res){ //handle database query results
		try {
			Connection_ID = res.getInt(1);
			Source_ID = res.getString(3);
			Source_Server=res.getString(4);
			Target_ID = res.getString(5);
			Target_Server = res.getString(6);
			Type = res.getString(7); //protocol, ie ssh, sftp
			Source_LZ = res.getString(8);
			if (Source_LZ.equals("$HOME")){
				//System.out.println("inputDB converting $HOME");
				Source_LZ = System.getenv(Source_LZ.substring(1, Source_LZ.length()));
			}
			Target_LZ = res.getString(9);
			if (Target_LZ.equals("$HOME")){
				//System.out.println("inputDB converting TARGET $HOME");
				setTargetLZ(Target_LZ);
			}
			Description = res.getString(10);
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*for testing the tests on centos
	public LZ_Details(String suser, String shost, String user, String host, String protocol, String LZ1, String LZ2, String desc) {
		Source_ID  = suser;
		Source_Server = shost; 
		Target_ID = user;
		Target_Server = host;
		Type = protocol; 
		if (LZ1.equals("$HOME")){
			System.out.println("inputDB converting $HOME");
			Source_LZ = System.getenv(LZ1.substring(1, LZ1.length()));
		}
		else {	Source_LZ = LZ1; }
		if (LZ2.equals("$HOME")){
			System.out.println("converting $HOME");
			setTargetLZ(LZ2);
		}
		else {Target_LZ = LZ2;} 
		Description = desc; 
	} */
	
	
	public void setTargetLZ(String target){
		String command = "ssh -o \"BatchMode=yes\" " + Target_ID + "@" + Target_Server + " env | grep HOME | cut -d'=' -f2 "; //workaround bash evaluating $HOME on source server
		try{ 
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String targ = br.readLine();
			//System.out.println("setting target lZ  " + targ);
			this.Target_LZ = targ;  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getSourceID() {
		return Source_ID;
	}
	
	public String getSourceServer() {
		return Source_Server;
	}
	public String getTargetID() {
		return Target_ID;
	}
	
	public String getTargetServer() {
		return Target_Server;
	}
	
	public String getType() {
		return Type;
	}
	
	public String getSourceLZ() {
		return Source_LZ;
	}
	
	public String getTargetLZ() {
		return Target_LZ;	//what changes should it be made here?
	}
	
	public String getDescription() {
		return Description;
	}
	public int getConnID(){
		return Connection_ID;
	}
///not sure these will be needed if augustine will send list of connections to test. 
	public String getRegion() {
		String[] tmp = this.getDescription().split("_");
		String region = tmp[3].trim();
		return region;
	}
	public String getEnvironment() {
		String[] tmp = this.getDescription().split("_");
		String env = tmp[0].trim();
		return env;
	}
	
	public void print_LZ_Details() {  
		System.out.println("Source: " + Source_ID + "@" + Source_Server + "\n" + 
						"Target: " + Target_ID + "@" + Target_Server + "\n"
						 + "Protocol Type: " + Type + "   "
						 + "Source LZ: " + Source_LZ + "        "
						 + "Target_LZ: " + Target_LZ + "\n"			
						 + "Description: " + Description);
	}

}
