package socket;
import javax.swing.*;
import java.awt.*;
import com.jcraft.jsch.*;

public class Connection_check {
String user;
String host;
int port;

	void run(String args[]) {
		user=args[0];
		host=args[1];
		port=22;
		
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
			
		}
		catch(Exception e){
			e.printStackTrace(System.out);
			//update db with failure. try a text file for now. 
		}
		
	}
	public static void main(String args[]){
		Connection_check tryit = new Connection_check();
	    String arglist[] = {"vivian", "192.168.128.135"};
		tryit.run(arglist);
	}
}
