package socket;
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
			e.printStackTrace(System.out);
			failures.add(server); 
		}
		
	}
	public static void main(String args[]){
		Connection_check tryit = new Connection_check();
	  //  String arglist[] = {"vivian", "192.168.128.135"};
	    //write a function to get the filtered list from dB
	    //call run on every server
	    //
		//tryit.run(arglist);
	//	System.out.println("1: " + failures);
		
		Get_list a = new Get_list(); 
		List<String[]> serverlist = a.go();
		for (int i=0; i<serverlist.size(); i++){
			tryit.run(serverlist.get(i));
		}
	}
}
