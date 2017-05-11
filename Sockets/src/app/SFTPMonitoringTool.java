package app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
public class SFTPMonitoringTool {
	
	public void inputTest(){ //create your list of LZ_Details objects
		List<LZ_Details> testList = new ArrayList<LZ_Details>();
		String protocol="jdbc:sqlite:";
		String dbName="/home/vivian/Desktop/kpccmt/db/development.sqlite3";
		String query= "SELECT * FROM connections WHERE id=1" +" AND component_id=1";
	
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
		}
		test_connections(testList.get(0));
	}
	public void test_connections(LZ_Details lz){
		lz.print_LZ_Details();
		ToolManager tm;
		tm = new ToolManager(lz.getTargetID(),lz.getTargetServer());
		int mainStatusCode = tm.ping_target();
		System.out.println("pinged: " + mainStatusCode);
		//if source server is valid (exists, is pingable)
		// I AM the source server, so don't need to ssh to it and then run tests, just run tests. 
	}
	public static void main(String[] args){
		SFTPMonitoringTool a = new SFTPMonitoringTool();
		a.inputTest();
	}
}
