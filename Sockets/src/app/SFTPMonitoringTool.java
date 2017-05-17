package app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import com.jcraft.jsch.*;
public class SFTPMonitoringTool {
	
	public void inputTest(){ //create your list of LZ_Details objects
		/*List<LZ_Details> testList = new ArrayList<LZ_Details>();
		String protocol="jdbc:sqlite:";
		//String dbName= "/home/shagent/rails/kpccmt/db/development.sqlite3";  
		String dbName="/home/vivian/Desktop/kpccmt/db/development.sqlite3";
		String query= "SELECT * FROM connections WHERE id=1" ; //and source server is myself, and env, region, comp. 
	
		try(	Connection conn = DriverManager.getConnection(protocol + dbName);)
		
		{
			System.out.println("connected to the list DB");
			try (Statement s=conn.createStatement(); 
					ResultSet res=s.executeQuery(query);)
			{
				while (res.next()){
					LZ_Details a = new LZ_Details(res);
					testList.add(a);
				}
			}
		} 
		catch (Exception e){ 
			e.printStackTrace();
		}*/
		//testList.get(0).print_LZ_Details();
		//use tester when you just want to see the connection tests run.
		LZ_Details tester = new LZ_Details("vivian", "192.168.128.136", "vivian", "192.168.128.135","SFTP", "HOME", "HOME", "testing123");
		//use test1 if you're getting lz_details from db.
	//	LZ_Details test1 = new LZ_Details("kpccjobs","ftpkpcc-dit.ssdc.kp.org", "SSH", "$HOME","$HOME","DIT_Connection #1_ETL_CO");
		test_connections(tester);// use this when you're ready to tun on app server/source servers*/
	}
	
	public void test_connections(LZ_Details lz){
		lz.print_LZ_Details();
		ToolManager tm;
		tm = new ToolManager(lz.getTargetID(), lz.getTargetServer());
		int mainStatusCode = tm.ping_target();
		System.out.println("pinging " +lz.getTargetServer() + ", status: " + mainStatusCode);
		//IF TARGET IS VALID,
		if (mainStatusCode == 0){
			mainStatusCode = tm.targetServerConnection(lz.getType());
			System.out.println(lz.getType()+ " connection: " + mainStatusCode);
			//TODO validate landing zones for sftp tests
		}
	}
	public static void main(String[] args){
		SFTPMonitoringTool a = new SFTPMonitoringTool();
		a.inputTest();
	}
}
