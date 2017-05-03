package socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.jcraft.jsch.*;

public class Connection_check {
String user;
String host;
int port;
static ArrayList<String> failures=new ArrayList<String>();;

	void run(String[] args) { 
		user=args[0];
		host=args[1];
		port=22;
		String server=user+"@"+host;
		System.out.println(server);
		try{
			JSch jsch= new JSch();
			jsch.addIdentity("~/.ssh/id_rsa");
			Session session=jsch.getSession(user,host,port);
			session.setConfig("PreferredAuthentications", "publickey");
			session.setConfig("StrictHostKeyChecking", "no");
			System.out.println("Establishing Connection to "+user + "@"+host);
			session.connect();
			System.out.println("connection established");
			session.disconnect();
			System.out.println("connection closed");
		}
		catch(Exception e){
			System.out.println("connection failed");
			failures.add(server); 
		}
		
	}
	
	List<String[]> getlist(String region, String env){
		List<String[]> servers=new ArrayList<String[]>(); 
		String protocol="jdbc:derby:";
		String dbName="/home/vivian/git/Sockets/Sockets/ServerList";
		
		String query= "SELECT use, name FROM Servers_long WHERE region=" + region +" AND env=" + env; 
		
		//Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		try(	Connection conn = DriverManager.getConnection(protocol + dbName);)
		{
			System.out.println("connected to the DB");
			try (Statement s=conn.createStatement(); 
					ResultSet res=s.executeQuery(query);)
			{
				while (res.next()){
					String [] a = {res.getString("use"), res.getString("name")};
					servers.add(a); 
				}
			}
		}
		catch (SQLException sqle)
	    {
	        sqle.printStackTrace(System.out);
	    }
		catch (Exception e){
			e.printStackTrace();
		}

		return servers;
	}
	
	public static void main(String args[]){
		Connection_check tryit = new Connection_check();
		
		List<String[]> serverlist = tryit.getlist("'SW'", "'PROD'");
		for (int i=0; i<serverlist.size(); i++){
			System.out.println("server:" + serverlist.get(i)[1]);
			//tryit.run(serverlist.get(i));
		}
		
		System.out.println("failed: " + failures); 
	}
}
