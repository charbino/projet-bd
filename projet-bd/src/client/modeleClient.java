package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import BD.DbConnection;

//classe qui permet de gerer l'accès au donnée de client 
public class modeleClient {

	//permet de recupererer les vélo disponible d'une station
	public static HashMap getVeloLibreStation(String adresseStation) throws SQLException{
		System.out.println("not implemented");
		
		String sql="select id_bornette "
				+  "from Bornette "
				+  "where adresse_station='"+adresseStation+"' "
				+  "and id_velo in (select id_velo from velo where etat_velo='OK');";
		
		System.out.print("requete : "+ sql);
		
		HashMap listeVeloLibreBornette = null;
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		int i=1;
		while(result.next()){
			
			listeVeloLibreBornette.put(i, result.getObject(i));
			
			i++;
		}
		
		return listeVeloLibreBornette;
		
		
	}
	
}
