package app;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class SFTPMonitoringTool {

	public void inputTest(int comp){ 
		/*
		List<LZ_Details> testList = new ArrayList<LZ_Details>();
		String url="jdbc:mysql://172.17.119.160:3306/kpccmt_db";
	
		String query= "SELECT * FROM connections WHERE component_id=" +comp + " AND target_server=\"szaddb88.ssdc.kp.org\" "; 
		try
		{
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection conn=DriverManager.getConnection(url,"shagent", "kpccmt");
			System.out.println("connected to the list DB");
			Statement s=conn.createStatement(); 
			ResultSet res=s.executeQuery(query);
			while (res.next()){
				LZ_Details a = new LZ_Details(res);
				testList.add(a);
			} 
		} 
		catch (Exception e){ 
			e.printStackTrace();
		}
		
		for (LZ_Details lz : testList){
			lz.print_LZ_Details();
		}
		test_connection(testList.get(0));*/
	
	}
	public static void test_connection(LZ_Details lz){
	//	lz.print_LZ_Details();
		ToolManager tm;
		tm = new ToolManager(lz);
		//Test for valid server, test ssh connection
		if (tm.ping_target()){
			//SSH or SSH2 protocol
			if (lz.getType().equalsIgnoreCase("SSH") || lz.getType().equalsIgnoreCase("SSH2")){
			
				boolean[] ssh_success=tm.targetSSHConnection();
				System.out.println("successful ssh? " + Arrays.toString(ssh_success));
				//update db
			}
			
			//sftp transfer tests
			else if (lz.getType().equalsIgnoreCase("sftp") || lz.getType().equalsIgnoreCase("sftp2")){
				boolean[] sftp_success = tm.SFTPTransferValidation();
				System.out.println("SFTPTransferTest: " + Arrays.toString(sftp_success));
				//update db
			}
			else { //skip
			}
		}
		else {
			System.out.println("invalid server, [false, false, false]");
		}
		
	}
	public static void main(String[] args){
		//SFTPMonitoringTool a = new SFTPMonitoringTool();
		//a.inputTest(77);
		LZ_Details lz = new LZ_Details("vivian", "192.168.128.140", "connection", "192.168.128.139", "sftp", "$HOME", "$HOME", "asfa");
		test_connection(lz);
	}
}
