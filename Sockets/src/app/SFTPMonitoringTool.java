package app;

public class SFTPMonitoringTool {
	
	public static void test_connection(LZ_Details lz){
		lz.print_LZ_Details();
		ToolManager tm;
		tm = new ToolManager(lz.getType(), lz.getSourceLZ(), lz.getTargetLZ(), lz.getTargetID(), lz.getTargetServer());
		int validServer = tm.ping_target();
		System.out.println("pinging " +lz.getTargetServer() + ", status: " + validServer);
		int validConnection = tm.targetServerConnection();
		System.out.println(lz.getType() + " connection: " + validConnection);
		if (lz.getType().equalsIgnoreCase("sftp")){
			int validTransfer = tm.fileTransferValidation();
			System.out.println("transfer was : " + validTransfer);
			if (validTransfer != 0){
				boolean validLZ = tm.targetLZValidation();
				System.out.println("validLZ: " + validLZ);
			}
			//if file transfer fails but lz is valid then its a permission issue. 
			
		}
		/*
		//IF TARGET IS VALID,
		if (mainStatusCode == 0){
			mainStatusCode = tm.targetServerConnection(protocol);
			System.out.println(lz.getType()+ " connection: " + mainStatusCode);
			//IF CONNECTION IS SUCCESSFUL and SFTP
			if (mainStatusCode == 0 && protocol.equalsIgnoreCase("SFTP")){
				 // 
				tm.targetLZValidation();
				//mainStatusCode = tm.fileTransferValidation(protocol);
				//System.out.println("SFTP transfer validation: " + mainStatusCode); 
			}
		}*/
	}
	public static void main(String[] args){
		LZ_Details tester = new LZ_Details(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
		//LZ_Details tester = new LZ_Details("vivian", "192.168.128.140", "connection", "192.168.128.139", "sftp" ,"$HOME" ,"$HOME", "asfa");
		test_connection(tester);
	}
}
