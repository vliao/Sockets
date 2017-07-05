package socket;
import java.io.*;
import java.net.*;
import java.net.InetAddress;
public class Requester {
	
	Socket requestSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	String server_message;
	String client_message;
	int port;
	Requester(){};
	InetAddress server= null; 
	void run(){
		try{
			//1. create socket
			System.out.println("server/IP: " + server);
			requestSocket = new Socket(server, port);
			System.out.println("connected to localhost port " + port);
			//2. get IO streams
			out = new PrintWriter(requestSocket.getOutputStream(),true);
			out.flush();
			in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			// 3. communicate with server
			server_message = (String)in.readLine(); 
			System.out.println("server> "+server_message);
			sendMessage("Hello server");
			
			do{
				
				//keep asking for more commands until bye? 
				System.out.println("what would you like to do");
				server_message = stdIn.readLine();  //input from client
				sendMessage(server_message); //send it to server
				String result;
				while ((result=in.readLine())!=null && !result.equals("alldone")){  
					System.out.println("server1> " + result);
				}
				System.out.println("done printing results");
			}while(!server_message.equals("bye"));
			
			
		}catch(UnknownHostException unknownHost){
			System.err.println("unknown host");
		}catch(IOException e){
			System.out.println("IOException caught!");
		}
		finally{
			try{ 
				in.close();
				out.close();
				requestSocket.close();
			} catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	void sendMessage(String msg){
		out.println(msg); //this sends the message to the server
		out.flush();
		System.out.println("client> "+msg); //this prints to our own terminal, requester.
	}
	void setInetAddress (String address){
		try {
			server = InetAddress.getByName(address);
		} catch(UnknownHostException e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		Requester client = new Requester();
		client.setInetAddress(args[0]);
		client.port = Integer.parseInt(args[1]);
		client.run();
	}
}