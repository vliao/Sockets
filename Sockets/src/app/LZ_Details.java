package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/* contains parameters gathered from the database
 * used to execute SFTP/LZ Connection check. 
 */


public class LZ_Details {
	private String Target_ID;
	private String Target_Server;
	private String Type;
	private String Source_LZ;
	private String Target_LZ;
	private String Description;
	
	public LZ_Details(ResultSet res){
		try {
			Target_ID = res.getString(5);
			Target_Server = res.getString(6);
			Type = res.getString(7); //protocol, ie ssh, sftp
			Source_LZ = res.getString(8);
			Target_LZ = res.getString(9);
			Description = res.getString(10);
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		System.out.println("Target: " + Target_ID + "@" + Target_Server + "\n"
						 + "Protocol Type: " + Type + "   "
						 + "Source LZ: " + Source_LZ + "        "
						 + "Target_LZ: " + Target_LZ + "\n"			
						 + "Description: " + Description + "\n");
	}

}
