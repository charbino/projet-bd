package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
		

		
		String sql="Select count(*) from abonne where code_secret='"+codeSecret+"'";
		
		//System.out.println("INFO : requete : "+ sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
	
		return result.next();
		

	}

	//verifie que le client possède un abonnement valide
	//pre-requis : l'abonnement existe déja
	public static boolean abonnementValide(String codeSecret) throws SQLException {
		
		String sql ="select * from abonne where code_secret='"+codeSecret+"' and add_months(DATE_DEBUT_ABONNEMENT,12) >= sysdate";
		//System.out.println("INFO : requete : "+ sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		//permet de savoir si il existe dans la table
		return result.next();
	}

	public static boolean codeSecretNonAbonneUnique(int codeSecret) throws SQLException {
		

		String sql ="select count(*) from nonAbonne where code_secret_non_abonne="+codeSecret;
		
		System.out.println("INFO : requete : "+ sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		return result.next();
	}

	public static void insererNonAbonne(int codeSecretNonAbonne, int cBClient) throws SQLException {
		
		Connection connection = DbConnection.getInstance();
		
		//on insere dans la table client 
		UUID idUniqueClient = UUID.randomUUID();
		System.out.println("id : "+idUniqueClient);
		
		//--------début de la transaction--------------------------
		connection.setAutoCommit(false);
		
		String sql ="insert into Client values('"+idUniqueClient+"',null)";
		
		System.out.println("INFO : requete : "+ sql);
		
		
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		prepare.executeUpdate();
		
		//Insertion dans la table nonAbonne

		String sql2 ="insert into nonAbonne values("+cBClient+",'"+idUniqueClient+"','"+codeSecretNonAbonne+"')";

		System.out.println("INFO : requete : "+ sql2);
		
		PreparedStatement prepare2 = connection.prepareStatement(sql2);
		
		prepare2.executeUpdate();
		
		connection.commit();// c'est ici que l'on valide la transaction
		connection.setAutoCommit(true);
		
		//--------Fin de la transaction--------------------------
	}
	
}
