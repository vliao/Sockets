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
			//3. communicate with server
			do{
				server_message = (String)in.readLine(); 
				System.out.println("server> "+server_message);
				sendMessage("Hello server");
				
				//figure out how this process should look, keep asking for more commands until bye? 
				//for now, just accepts one command. maybe require to preface it with cmd.exe 
				System.out.println("what would you like to do");
				String inputline = stdIn.readLine();
				sendMessage(inputline);
				
				//sendMessage("list");
				server_message = "bye";
				sendMessage(server_message);
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
		out.println(msg); //this sends the message to the server i think
		out.flush();
		System.out.println("client1> "+msg); //this prints to our client command window
	}
	public static void main(String args[]){
		Requester client = new Requester();
		client.run();
	}
}