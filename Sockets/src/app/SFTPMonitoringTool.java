package app;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

public class SFTPMonitoringTool {
	private Connection conn;
	private PreparedStatement s;
	private ResultSet res;
	
	public void inputTest(int comp){ 
		List<LZ_Details> testList = new ArrayList<LZ_Details>();
		String url="jdbc:mysql://172.17.119.160:3306/kpccmt_db";
	
		String query= "SELECT * FROM connections WHERE component_id=" +comp + " AND target_server=\"szaddb88.ssdc.kp.org\" "; 
		try
		{
			Class.forName("com.mysql.jdbc.Driver"); 
			conn=DriverManager.getConnection(url,"shagent", "kpccmt");
			System.out.println("connected to the list DB");
			s=conn.prepareStatement(query); 
			res=s.executeQuery();
			while (res.next()){
				LZ_Details a = new LZ_Details(res);
				testList.add(a);
			} 
			
			for (LZ_Details lz : testList){
			lz.print_LZ_Details();
			Status_Details result = test_connection(lz, lz.getConnID()); 
			update_connection(result);
			}
		} 
		catch (Exception e){ 
			e.printStackTrace();
		}
		finally {
			disconnectDB();
		}
	}
	public Status_Details test_connection(LZ_Details lz,  int id){
		Status_Details test_result = new Status_Details(id);
		//lz.print_LZ_Details();
		ToolManager tm;
		tm = new ToolManager(lz);
		boolean[] result1 = new boolean[3];
		//Test for valid server, test ssh connection
		if (tm.ping_target()){
			//SSH or SSH2 protocol 
			if (lz.getType().equalsIgnoreCase("SSH") || lz.getType().equalsIgnoreCase("SSH2")){
			
				result1=tm.targetSSHConnection();
				System.out.println("successful ssh? " + Arrays.toString(result1));
				//update db
			}
			
			//sftp transfer tests
			else if (lz.getType().equalsIgnoreCase("sftp") || lz.getType().equalsIgnoreCase("sftp2")){
				result1 = tm.SFTPTransferValidation();
				System.out.println("SFTPTransferTest: " + Arrays.toString(result1));
				//update db
			}
			else { //skip
			}
		}
		else {
			System.out.println("invalid server, [false, false, false]");
		}
		test_result.setSuccess(result1[0] ? 1 : 0);
		test_result.setPerm(result1[1] ? 1 : 0 );
		test_result.setLZ(result1[2] ? 1 : 0);
		return test_result;
	}
	
	public void update_connection(Status_Details sd){//updates but doesn't handle the case that the connection is not already in the table
		String time = now();
		String update_stmt = "UPDATE connection_statuses set successful=" + sd.getSuccess() +", lz_permission=" + sd.getPerm() + ", valid_lz=" 
				+ sd.getLZ() +", updated_at=" + time + " WHERE connection_id=" + sd.getID();
		System.out.println(update_stmt+"\n");
		try { 
			s=conn.prepareStatement(update_stmt);
			s.executeUpdate();
		}
		catch (SQLException e){
			e.printStackTrace();
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
				e.printStackTrace();
			}
		}
		if (s!=null){
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null){
			try {
				conn.close();
			} catch (SQLException e ){
				e.printStackTrace();
			}
		}
	}
	/*public static void main(String[] args){
		//SFTPMonitoringTool a = new SFTPMonitoringTool();
		//a.inputTest(77);
		//LZ_Details lz = new LZ_Details("vivian", "192.168.128.140", "connection", "192.168.128.139", "sftp", "$HOME", "$HOME", "asfa");
		String time = now();
		System.out.println(time);
	}*/
}
