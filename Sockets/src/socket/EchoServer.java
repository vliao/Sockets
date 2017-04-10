package socket;
import java.net.*;
import java.io.*;

public class EchoServer {
	public static void main(String[] args) throws IOException{
		
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>"); 
			System.exit(1);
		}
		int portNumber = Integer.parseInt(args[0]);
		
		try (
				ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				Socket clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
			){
				System.out.println("Connection created");
				String inputLine;
				while ((inputLine = in.readLine()) != null){
					out.println(inputLine);
				}
		} catch (IOException e){
			System.out.println("Exception caught when trying to listen on port " + portNumber + 
					"or listening for a connection");
			System.out.println(e.getMessage());
			}
	}
}

/*have java communicate with the shell, ie client says ls -l, need server side to run ls- l, return restults. 
		run EchoServer as a service, just listenin. not always running, jsut waiting for a command on the port 
		
		*/