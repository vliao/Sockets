package app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Ping {  //used by toolmanager
	//first tries to ping a given address
	public static int ping(String serverName, int timeout, int port){
		int statusCode = 0;
		Socket socket = new Socket();
		InetAddress inetAddress = null;
		//validate server name
		try {
			inetAddress = getInetAddress(serverName);
		} catch (IOException e) {  //If exception is thrown, server name does not exist
			//System.out.println( "FAILURE: INVALID ADDRESS @ " + serverName + " [" + inetAddress + "]" ); 
			statusCode = 1;
		}
		
		//ATTEMPT TO PING SERVER
		try{
			socket.connect(new InetSocketAddress(inetAddress, port), timeout);
		} catch (NullPointerException e){
			//INVALID ADDRESS
			statusCode = 1;
		}
		catch (SocketTimeoutException e) {
			//Connection exception, timeout
			statusCode = 2;
		} catch (IOException e) {
			System.out.println( "IOException in ping" );
			statusCode = 3;
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				//System.out.print(" Socket closure exception ");
			}
		}
		return statusCode;
	}

	public static InetAddress getInetAddress(String serverName) throws UnknownHostException{
		InetAddress inetAddress = null;
		inetAddress = InetAddress.getByName(serverName);
		return inetAddress;
	}
}
