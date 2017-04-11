package socket;
import java.io.*;
import java.net.*;
public class Requester {
	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	Requester(){};
	void run(){
		try{
			//1. create socket
			requestSocket = new Socket("localhost", 9999);
			System.out.println("connected to localhost port 9999");
			//2. get IO streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			//3. communicate with server
			do{
				try{
					message = (String)in.readObject();
					System.out.println("server> "+message);
					sendMessage("Hello server");
					sendMessage("list");
					message = "bye";
					sendMessage(message);
				} catch(ClassNotFoundException classNot){
					System.out.println("data rec'd in unknown format");
				}
			}while(!message.equals("bye"));
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
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client> "+msg);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		Requester client = new Requester();
		client.run();
	}
}