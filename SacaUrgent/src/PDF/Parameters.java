package PDF;

public class Parameters {

	private static String PathToSave= "//192.168.90.203/Logistyka/Listy";
	private static String PathToDB = "//192.168.90.203/Logistyka/Tosia/Projekty JAVA";
	
	public static String getPathToSave(){
		return PathToSave;
	}
	
	public static void setPathToSave (String s){
		PathToSave = s;
	}

	public static String getPathToDB() {
		return PathToDB;
	}

	public static void setPathToDB(String pathToDB) {
		PathToDB = pathToDB;
	}
}
