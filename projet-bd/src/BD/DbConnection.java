package BD;

import java.sql.Connection;
import java.sql.DriverManager;


//classe qui permet de gérer la connection à la base de donnée
public class DbConnection {
	
	private static Connection instanceBD;
	private static String url = "jdbc:oracle:thin:@im2ag-oracle.e.ujf-grenoble.fr:1521:ufrima";
	private static String user = "framines";
	private static String passwd = "bd2015";
	
	
	  public static Connection getInstance() {      
		  try {
	    	if(instanceBD==null){
	    		instanceBD = DriverManager.getConnection(url, user, passwd);
	    		System.out.println("INFO : Connexion à la base de donnée effectué");  
	    	}
	         
		  }catch (Exception e) {
	      e.printStackTrace();
	    }     
	    return instanceBD;
	  }
}
