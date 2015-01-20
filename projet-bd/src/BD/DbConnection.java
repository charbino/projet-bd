package BD;

import java.sql.Connection;
import java.sql.DriverManager;


//classe qui permet de gérer la connection à la base de donnée
public class DbConnection {
	
	private static Connection instanceBD = null;
	private static String url = "jdbc:oracle:thin:@im2ag-oracle.e.ujf-grenoble.fr:1521:ufrima";
	private static String user = "framines";
	private static String passwd = "bd2015";
	
	  public static Connection getInstance() {      
		  try {
	    	if(instanceBD==null){
	    		Connection conn = DriverManager.getConnection(url, user, passwd);
	    		System.out.println("--Connexion à la base de donnée effectué--");  
	    	}
	         
		  }catch (Exception e) {
	      e.printStackTrace();
	    }     
	    return instanceBD;
	  }
}
