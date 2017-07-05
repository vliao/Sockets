package socket;
import java.net.*;
import java.util.Properties;

//import app.LZ_Details;
import app.SFTPMonitoringTool;

import java.io.*;

public class Provider {
	ServerSocket providerSocket;
	private static String user="";
	private int portNumber=9999;
	Socket connection=null;
	PrintWriter out;
	BufferedReader in;
	String message;
	
	void run(){
		try{
			//1. create a server socket
			providerSocket = new ServerSocket(portNumber);
			//2. wait for connection
			System.out.println("Waiting for connection on " + portNumber);// + " on " + providerSocket.getInetAddress());
			connection = providerSocket.accept();
			//3.get I/O streams
			out = new PrintWriter(connection.getOutputStream(),true);
			out.flush();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sendMessage("Connection Successful");
			//4. communicate with client via IO stream
			//initial hello sequence
			message=(String)in.readLine();  //receiving out.println(msg) of requester here. 
			System.out.println("client> " +message);
			do {
					//now communication loop, executing commands 
					message=(String)in.readLine();
					System.out.println("client> " +message);
					
					if (message.equals("bye")){ //if client says bye, respond bye as well. 
						sendMessage("bye");
					}
					
					//either bye or a command
					else {
							System.out.println("testing connections for component_id=" + message);
							
						    String[] args = message.split(" " );
							SFTPMonitoringTool domino = new SFTPMonitoringTool();
					    	domino.inputTest(Integer.parseInt(args[0]));
							/*Process p  = Runtime.getRuntime().exec(message);
							p.waitFor();
							
							BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
							String line;
							while ((line=r.readLine())!=null){
						            sendMessage(line);
				  		        }
							 r.close();*/
							 out.println("alldone");
							 System.out.println("done sending result");
					}
	
			}while(!message.equals("bye"));
				
		} catch (IOException e){
			System.out.println("Exception caught when trying to listen on port " + portNumber + 
					" or listening for a connection");
			e.printStackTrace();
		} 
		finally{
			try{ //when client says bye, close these  
				in.close();
				out.close();
				providerSocket.close();
			}
			catch(IOException e){
				System.out.println("io exception");
			}
		}
	}	
	void sendMessage(String msg){
		out.println(msg); //send to client, sits in clients inputstream, read with in.readLine. prints in requester terminal
		out.flush();
		//System.out.println("server> " + msg);	//this prints in provider terminal
	}
	void setProperties(){
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			user = (prop.getProperty("user"));
			portNumber = Integer.parseInt(prop.getProperty("port"));
			System.out.println(portNumber);
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally { 
			if (input != null){
				try { 
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static String getUser (){
		return user; 
	}
	
	public static void main(String args[]){
		Provider server = new Provider();
		server.setProperties();
		while(true){
			server.run();
		}
	}
	
}