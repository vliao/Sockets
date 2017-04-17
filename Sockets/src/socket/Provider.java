package socket;
import java.net.*;
import java.io.*;

public class Provider {
	ServerSocket providerSocket;
	int portNumber=9999;
	Socket connection=null;
	PrintWriter out;
	BufferedReader in;
	String message;
	
	void run(){
		try{
			//1. create a server socket
			providerSocket = new ServerSocket(portNumber);
			//2. wait for connection
			System.out.println("Waiting for connection");
			connection = providerSocket.accept();
			//3.get I/O streams
			out = new PrintWriter(connection.getOutputStream(),true);
			out.flush();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			sendMessage("Connection Successful");
			//4. communicate with client via IO stream
			//initial hello sequence
			message=(String)in.readLine();
			System.out.println("client> " +message);
			do {
					//now communication loop, executing commands 
					message=(String)in.readLine();
					System.out.println("client> " +message);
					
					/*if (message.equals("list")) { //or whatever your cue is to execute command line commands on this server
						System.out.println("executing a command");
						ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd");
						builder.redirectErrorStream(true);
						java.lang.Process p = builder.start(); 
						BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line;
						 while ((line=r.readLine())!=null){
					            sendMessage(line);
					            
					        }
						r.close();
					}*/
					if (message.equals("bye")){ //if client says bye, respond bye as well. 
						sendMessage("bye");
					}
					
					//either bye or a command
					else {
							System.out.println("executing a command");
							ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", message);
							builder.redirectErrorStream(true);
							java.lang.Process p = builder.start(); 
							BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
							String line;
							while ((line=r.readLine())!=null){
						            sendMessage(line);
						        }
							 r.close();
							 out.println("alldone");
							 System.out.println("done sending result");
					}
	
			}while(!message.equals("bye"));
				
		} catch (IOException e){
			System.out.println("Exception caught when trying to listen on port " + portNumber + 
					" or listening for a connection");
		}
		finally{
			try{
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
		out.println(msg); //send to client, sits in clients inputstream, read with in.readLine
		out.flush();
		System.out.println("server> " + msg);	
	}
	public static void main(String args[]){
		Provider server = new Provider();
		while(true){
			server.run();
		}
	}
	
}
/*have java communicate with the shell, ie client says ls -l, need server side to run ls- l, return results. 
		run EchoServer as a service, just listenin. not always running, jsut waiting for a command on the port 
	
	try java service wrapper
	
		*
client server basic app copied from http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets */