package app;

public class Status_Details {
	private int connection_id;
	private int successful;
	private int lz_permission;
	private int valid_lz;
	 
	 public Status_Details(int id){
		 connection_id = id;
		 successful = 0;
		 lz_permission = 0;
		 valid_lz = 0;
	 }
	 
	 public void setSuccess(int success){
		 this.successful = success;
	 }
	 public void setPerm(int perm){
		 this.lz_permission = perm;
	 }
	 public void setLZ(int lz){
		 this.valid_lz = lz;
	 }
	 
	 public int getSuccess(){
		 return  successful;
	 }
	 public int getPerm(){
		 return lz_permission;
	 }
	 public int getLZ(){
		 return valid_lz;
	 }
	 public int getID(){
		 return connection_id;
	 }
	 
	 public void print_status (){
		 System.out.println("Connection " + connection_id + ": Successful: " + successful + ", permission: " 
				 + lz_permission + ", valid_lz: " + valid_lz);
	 }
}
