package socket;
import java.net.*;
import java.io.*;

public class Provider {
	ServerSocket providerSocket;
	int portNumber=9999;
	Socket connection=null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	
	void run(){
		try{
			//1. create a server socket
			providerSocket = new ServerSocket(portNumber);
			//2. wait for connection
			System.out.println("Waiting for connection");
			connection = providerSocket.accept();
			//3.get I/O streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			sendMessage("Connection Successful");
			//4. communicate with client via IO stream
			do {
				try{
					message=(String)in.readObject();
					System.out.println("client> " +message);
					if (message.equals("list")) {
						System.out.println("executing a command");
						ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd");
						builder.redirectErrorStream(true);
						java.lang.Process p = builder.start(); 
						BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line;
						 while ((line=r.readLine())!=null){
					            System.out.println(line);
					        }
						r.close();
					}
					if (message.equals("bye")){
						sendMessage("bye");
					}
				}catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
			}while(!message.equals("bye"));
				
		} catch (IOException e){
			System.out.println("Exception caught when trying to listen on port " + portNumber + 
					" or listening for a connection");
			System.out.println(e.getMessage());
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
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("server> " + msg);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		Provider server = new Provider();
		while(true){
			server.run();
		}
	}
	
}
/*have java communicate with the shell, ie client says ls -l, need server side to run ls- l, return restults. 
		run EchoServer as a service, just listenin. not always running, jsut waiting for a command on the port 
	
		*
client server basic app copied from http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets */