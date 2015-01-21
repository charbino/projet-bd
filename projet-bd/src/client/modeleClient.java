package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import BD.DbConnection;

//classe qui permet de gerer l'accès au donnée de client 
public class modeleClient {

	//permet de recupererer les vélo disponible d'une station
	public static HashMap getVeloLibreStation(String adresseStation) throws SQLException{
		
		String sql="select * "
				+  "from Bornette "
				+  "where adresse_station='"+adresseStation+"' "
				+  "and id_velo in (select id_velo from velo where etat_velo='OK')";
		
		System.out.println("INFO : requete : "+ sql);
		
		HashMap listeVeloLibreBornette = new HashMap();
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		String id_bornette;
		String adresse_station;
		int i=1;
		while(result.next()){
			id_bornette		= result.getString("id_bornette");
			adresse_station = result.getString("adresse_station");
			
			
			listeVeloLibreBornette.put(i,id_bornette);
			
			i++;
		}
		
		result.close();
		prepare.close();
		
		return listeVeloLibreBornette;
		
		
		
		
	}

	public static boolean abonnementExiste(String codeSecret) throws SQLException {
		
		String sql="Select * from abonne where code_secret = '"+codeSecret+"'";
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		return result.first();
		

	}
	
}
