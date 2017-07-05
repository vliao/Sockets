package app;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import socket.SourceServer;
import java.util.List;
import java.util.Date;

public class SFTPMonitoringTool {
	private Connection conn;
	private PreparedStatement ps;
	private ResultSet res;
	
	
	public void inputTest(int comp){ 
		List<LZ_Details> testList = new ArrayList<LZ_Details>();
		Status_Details result = null;
		String user = SourceServer.getUser();
		String url="jdbc:mysql://172.17.119.160:3306/kpccmt_db";
		//select the rows matching your given component id and the source_name of the server (ie user) this service is being run by
		String query= "SELECT * FROM connections WHERE component_id=" +comp + " AND source_name=\"" + user +"\"";
		try
		{
			Class.forName("com.mysql.jdbc.Driver"); 
			conn=DriverManager.getConnection(url,"shagent", "kpccmt");
			System.out.println("connected to the list DB");
			ps=conn.prepareStatement(query); 
			res=ps.executeQuery();
			while (res.next()){
				//turn each result (ie connection row) into an lz object
				LZ_Details a = new LZ_Details(res);
				testList.add(a);
			} 
			
			//run tests on each lz object, update database for each lz object
			for (LZ_Details lz : testList){
				lz.print_LZ_Details();
				result = test_connection(lz); 
				update_connection(result);
			}
		} 
		catch (Exception e){ 
			System.out.println(e.getMessage());
		}
		finally {
			disconnectDB();
		}
	}
	
	public Status_Details test_connection(LZ_Details lz){
		Status_Details test_result = new Status_Details(lz.getConnID());
		TestManager tm=null;
		//create a tool manager for each lz object
		tm = new TestManager(lz);
		//Test for valid server, test ssh connection
		if (tm.ping_target()){
			//SSH or SSH2 protocol 
			if (lz.getType().contains("ssh")){
				test_result=tm.targetSSHConnection();
				System.out.println("successful ssh? " + test_result.getSuccess());
			}
			
			//sftp or sftp2
			else if (lz.getType().contains("sftp")){
				test_result = tm.SFTPTransferValidation();
				System.out.println("SFTPTransferTest: " + test_result.getSuccess());
			}
			else { //skip
			}
		}
		else {
			System.out.println("invalid server, [false, false, false]");
		}
		test_result.print_status();
		return test_result;
	}
	
	//updates if the connection is already in the table, if not, does nothing. 
	public void update_connection(Status_Details sd){		String time = now();
		String update_stmt = "UPDATE connection_statuses set successful=" + sd.getSuccess() +", lz_permission=" + sd.getPerm() + ", valid_lz=" 
				+ sd.getLZ() +", updated_at=" + time + " WHERE connection_id=" + sd.getID();
		System.out.println(update_stmt+"\n");
		try { 
			ps=conn.prepareStatement(update_stmt);
			ps.executeUpdate();
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static String now(){
		DateFormat dateFormat = new SimpleDateFormat("\"yyyy-MM-dd HH:mm:ss\"");
		Date date = new Date();
		String time = dateFormat.format(date);
		return time;
	}
	
	public void disconnectDB(){
		if (res!=null){
			try {
				res.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		if (ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		if (conn != null){
			try {
				conn.close();
			} catch (SQLException e ){
				System.out.println(e.getMessage());
			}
		}
	}
	/*public static void main(String[] args){
		SFTPMonitoringTool a = new SFTPMonitoringTool();
		a.inputTest(Integer.parseInt(args[0]));
		//LZ_Details lz = new LZ_Details("vivian", "192.168.128.140", "connection", "192.168.128.139", "ssh", "$HOME", "$HOME", "asfa");
	//	test_connection(lz);
	}*/
}
