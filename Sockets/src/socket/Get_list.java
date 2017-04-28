package socket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Properties;


public class Get_list {
	ArrayList<String> go(){
		ArrayList<String> servers=new ArrayList<String>(); 
		String protocol="jdbc:derby:";
		//String dbName = "/home/vivian/workspace/simple/MyDbTest";
		String dbName="/home/vivian/git/Sockets/Sockets/ServerList";
		ResultSet res=null;
		Connection conn=null;
		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			conn = DriverManager.getConnection(protocol + dbName);
			System.out.println("connected to the DB");
			Statement s; 
			s=conn.createStatement();
			//res=s.executeQuery("SELECT num, addr FROM derbyDB2");
			res=s.executeQuery("SELECT name FROM Servers WHERE region='NW' ");
			while (res.next()){
				servers.add(res.getString(1)); //Int or String
			}
			
		}
		catch (SQLException sqle)
	    {
	        sqle.printStackTrace(System.out);
	    }
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			try{
			res.close(); 
			conn.close();
			}
			catch (SQLException e){
				e.printStackTrace(System.out);
			}
		}
		
		return servers;
	}
	
	
	
	public static void main(String[] args){
		Get_list a = new Get_list();
		ArrayList<String> numbers = new ArrayList<String>();
		numbers = a.go();
		System.out.println("servers in the list: " + numbers);
	}
	
}
