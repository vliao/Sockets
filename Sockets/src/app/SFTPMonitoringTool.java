package app;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class SFTPMonitoringTool {

	public void inputTest(int comp){ 
		
		List<LZ_Details> testList = new ArrayList<LZ_Details>();
		String url="jdbc:mysql://172.17.119.160:3306/kpccmt_db";
	
		String query= "SELECT * FROM connections WHERE component_id=" +comp ; 
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
	}
	public static void test_connection(LZ_Details lz){
		lz.print_LZ_Details();
		ToolManager tm;
		tm = new ToolManager(lz.getType(), lz.getSourceLZ(), lz.getTargetLZ(), lz.getTargetID(), lz.getTargetServer());
		int validServer = tm.ping_target();
		System.out.println("pinging " +lz.getTargetServer() + ", status: " + validServer);
		int validConnection = tm.targetServerConnection();
		System.out.println(lz.getType() + " connection: " + validConnection);
		if (lz.getType().equalsIgnoreCase("sftp")){
			int validTransfer = tm.fileTransferValidation();
			System.out.println("transfer was : " + validTransfer);
			if (validTransfer != 0){
				boolean validLZ = tm.targetLZValidation();
				System.out.println("validLZ: " + validLZ);
			}
			//if file transfer fails but lz is valid then its a permission issue. 
			
		}
		/*
		//IF TARGET IS VALID,
		if (mainStatusCode == 0){
			mainStatusCode = tm.targetServerConnection(protocol);
			System.out.println(lz.getType()+ " connection: " + mainStatusCode);
			//IF CONNECTION IS SUCCESSFUL and SFTP
			if (mainStatusCode == 0 && protocol.equalsIgnoreCase("SFTP")){
				 // 
				tm.targetLZValidation();
				//mainStatusCode = tm.fileTransferValidation(protocol);
				//System.out.println("SFTP transfer validation: " + mainStatusCode); 
			}
		}*/
	}
	public static void main(String[] args){
		SFTPMonitoringTool a = new SFTPMonitoringTool();
		a.inputTest(1);
		
	}
}
