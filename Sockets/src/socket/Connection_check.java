package socket;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import com.jcraft.jsch.*;
import com.jcraft.jsch.JSchException;

public class Connection_check {
	private JSch ssh_object;
	private String source_id;
	private String source_server;
	private Session session;
	private ConfigRepository configRepository;
	
	private int portNumber;
	private int timeout;
	private int exitStatus;	
/*	
String user;
String host;
int port;*/
static ArrayList<String> failures=new ArrayList<String>();

	public Connection_check(String source_id, String source_server){
		ssh_object = new JSch();
		session = null;
	/*	try{
			configRepository = com.jcraft.jsch.OpenSSHConfig.parseFile("~/.ssh/config");
		} catch(IOException e){
			e.printStackTrace();
		}*/
		portNumber = 22;
		timeout = 3000;
		exitStatus = 0;
		this.source_id = source_id;
		this.source_server = source_server;
	}
	/*
	public void startNewSession(){
		try{
			session = ssh_object.getSession(source_id, source_server, portNumber);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			//config.put("PreferedAuthentications", "publickey");
			session.setConfig(config);
			Config thisHostConfig = configRepository.getConfig(source_server);
			
			if (thisHostConfig != null){
				String[] identityFile = thisHostConfig.getValues("IdentityFile");
				if (identityFile != null && identityFile.length>0){
					for (String idFile : identityFile){
						ssh_object.addIdentity(idFile);
					}
				} else {
					ssh_object.addIdentity("~/.ssh/id_rsa");
				}
			}
			ssh_object.addIdentity("~/.ssh/id_rsa");
			session.connect(timeout);
			System.out.println("connection to " + source_server + " success");
		} catch (JSchException e){
			System.out.println( "JSchException at startNewSession(), timeout while connecting to source server");
			e.printStackTrace();
			failures.add(source_server);
		}
	}*/

	// get the list of servers that you want to test from your db
	public static List<String[]> getlist(String region, String env, String comp){
		List<String[]> servers=new ArrayList<String[]>(); 
		String protocol="jdbc:derby:";
		String dbName = "/home/vivian/git/Sockets/Sockets/ServerList";
		String query="SELECT id, server, protocol FROM Servers WHERE region="+region+"AND env="+env+" AND component="+comp;
				
	/*String protocol="jdbc:sqlite:";
		String dbName="/home/vivian/Desktop/kpccmt/db/development.sqlite3";
		String query= "SELECT source_name, source_server FROM connections WHERE id=1" +" AND component_id=1"; 
		*/
		try(	Connection conn = DriverManager.getConnection(protocol + dbName);)
		{
			System.out.println("connected to the list DB");
			try (Statement s=conn.createStatement(); 
					ResultSet res=s.executeQuery(query);)
			{
				while (res.next()){
					String [] a = {res.getString("id"), res.getString("server"), res.getString("protocol")}; //source_name, source_server
					//String [] a = {res.getString("source_name"), res.getString("source_server")};
					servers.add(a); 
				}
			}
			try { //from simpleapp
				DriverManager.getConnection("jdbc:derby:;shutdown=true"); }
			catch (SQLException se)
            {
                if (( (se.getErrorCode() == 50000)
                        && ("XJ015".equals(se.getSQLState()) ))) {
                    // we got the expected exception
                    System.out.println("Database shut down normally");
                    // Note that for single databasejavhutdown, the expected
                    // SQL state is "08006", and the error code is 45000.
                } else {
                    // if the error code or SQLState is different, we have
                    // an unexpected exception (shutdown failed)
                    System.err.println("Database did not shut down normally");
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
	
	/*public static void main(String args[]){
		
		List<String[]> serverlist = getlist("'NW'", "'PROD'", "'NEDI'");
		for (int i=0; i<serverlist.size(); i++){
			System.out.println("user: " + serverlist.get(i)[0] + "    server: " + serverlist.get(i)[1]);//+"  protocol: "+serverlist.get(i)[2]); //see what get list is giving you
			//Connection_check checker = new Connection_check(serverlist.get(i)[0],serverlist.get(i)[1] ); //try the connection
			//checker.startNewSession(); 
		}
		
		System.out.println("failed: " + failures); 
	}*/
}
