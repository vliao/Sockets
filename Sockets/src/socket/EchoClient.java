package socket;
import java.io.*;
import java.net.*;
public class EchoClient {

	public static void main(String[] args) throws IOException {
		if (args.length != 2){
			System.err.println( "Usage: java EchoClient <hostname> <port number>");
			System.exit(1);
		}
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		Process p;
		String command= "dir";
		try(
				Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out =new PrintWriter(echoSocket.getOutputStream(),true);			
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
		) {
		/*	p=Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line="";
			while ((line=in.readLine())!=null){
				System.out.println(line +"\n");
			}*/ 
			String userInput;
			while ((userInput =stdIn.readLine()) != null) {
				out.println(userInput);
				System.out.println("echo: "+ in.readLine());
			
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e){
			System.err.println("Couldn't get I/O for the connection to " +
					hostName);
			e.printStackTrace();
			System.exit(1);
		} /*catch (InterruptedException e){
			System.err.println("process error");
			System.exit(1);
		}*/
	}

}
