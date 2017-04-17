package socket;
import java.io.*;
import java.net.*;
public class Requester {
	Socket requestSocket;
	PrintWriter out;
	BufferedReader in;
	BufferedReader stdIn;
	String server_message;
	String client_message;
	Requester(){};
	void run(){
		try{
			//1. create socket
			requestSocket = new Socket("localhost", 9999);
			System.out.println("connected to localhost port 9999");
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
			e.printStackTrace();
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
		System.out.println("client1> "+msg); //this prints to our client command window
	}
	public static void main(String args[]){
		Requester client = new Requester();
		client.run();
	}
}