package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
		

		String sql="Select * from abonne where code_secret='"+codeSecret+"'";
		
		//System.out.println("INFO : requete : "+ sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
	
		return result.next();
		

	}
	
	public static Boolean nonAbonneExiste(String codeSecretNonAbonne) throws SQLException {

		
		String sql="Select * from nonabonne where CODE_SECRET_NON_ABONNE='"+codeSecretNonAbonne+"'";
		
		System.out.println("INFO : requete : "+ sql);
		
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
		

		String sql ="select * from nonAbonne where code_secret_non_abonne="+codeSecret;
		
		//System.out.println("INFO : requete : "+ sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		return !result.next();
	}

	public static String insererNonAbonne(int codeSecretNonAbonne, int cBClient) throws SQLException {
		
		Connection connection = DbConnection.getInstance();
		
		//on insere dans la table client 
		UUID idUniqueClient = UUID.randomUUID();
		//System.out.println("id : "+idUniqueClient);
		
		
		//--------début de la transaction--------------------------
		connection.setAutoCommit(false);
		
		String sql ="insert into Client values('"+idUniqueClient.toString()+"',null)";
		
		//System.out.println("INFO : requete : "+ sql);
		
		
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		prepare.executeUpdate();
		
		//Insertion dans la table nonAbonne

		String sql2 ="insert into nonAbonne values("+cBClient+",'"+idUniqueClient.toString()+"','"+codeSecretNonAbonne+"')";

		//System.out.println("INFO : requete : "+ sql2);
		
		PreparedStatement prepare2 = connection.prepareStatement(sql2);
		
		prepare2.executeUpdate();
		
		connection.commit();
		connection.setAutoCommit(true);
		
		//--------Fin de la transaction--------------------------
		
		return idUniqueClient.toString();
	}

	public static void creerLocation(String idCLient, String idBornetteChoisit,
			String adresseStation) throws SQLException {
		
		Connection connection = DbConnection.getInstance();
		connection.setAutoCommit(false);
		
		
		// -------- création une location------------
		UUID idLocation = UUID.randomUUID();
		//System.out.println("id : "+idLocation);
		
		//on recherche l'id du vélo
		String sqlVelo ="Select id_velo from bornette where id_bornette ='"+ idBornetteChoisit+"'";
		PreparedStatement prepareVelo = connection.prepareStatement(sqlVelo);
		ResultSet resultVelo = prepareVelo.executeQuery();
		
		if(! resultVelo.next()){
			System.out.println("Erreur sur le vélo");
		}
		else
		{
			String idVelo = resultVelo.getString("id_velo");
			String sqlInsertLocation ="insert into Location values('"+idLocation.toString()+"','"+idCLient+"',to_char( sysdate , 'YYYY' ),'"+idVelo+"','"+idBornetteChoisit+"',null,sysdate,null)";
			//System.out.println("INFO : requete : " +sqlInsertLocation);
			PreparedStatement prepareInsertLocation = connection.prepareStatement(sqlInsertLocation);
			
			prepareInsertLocation.executeUpdate();
			
			// -------- FIN création une location------------
			
			//-------Suppresion  velo de la bornette-----------
			String sqlSupprVeloBornette = "update Bornette set id_velo=null where id_velo='"+idVelo +"' and id_bornette='"+idBornetteChoisit +"'" ;
			System.out.println("INFO : requete : " +sqlSupprVeloBornette);
			PreparedStatement prepareSupprVeloBornette = connection.prepareStatement(sqlSupprVeloBornette);
			prepareSupprVeloBornette.executeUpdate();
			
			//-------Fin supprime le velo de la bornette-----------
			
			prepareInsertLocation.close();
			
		}
		
		
		connection.commit();
		connection.setAutoCommit(true);
		
	}

	public static String chercherCLientAbonne(String numAbonne) throws SQLException {
		// permet de chercher un client avec le code Secret
		String sql = "select id_client from abonne where code_secret='"+numAbonne+"'";
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		result.next();
		String idClient = result.getString("id_client");
		
		
		result.close();
		
		return idClient;
	}
	
	public static String chercherCLientNonAbonne(String codeSecretNonAbonne) throws SQLException {
		// permet de chercher un client avec le code Secret
		String sql = "select id_client from nonabonne where code_secret_non_abonne='"+codeSecretNonAbonne+"'";
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		result.next();
		String idClient = result.getString("id_client");
		result.close();
		
		return idClient;
	}

	public static String retrouverLocation(String idClient) throws SQLException {
		String sql = "select id_location from location where id_Client='"+idClient+"' and DATE_HEURE_FIN_LOCATION is null";
		
		//System.out.println("INFO : requete : "+sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		String idLocation="null";
		
		if (result.next()){
			 idLocation = result.getString("id_location");
		}
		
		result.close();
		
		return idLocation;
	}

	public static boolean placeDispoStation(String adresseStation) throws SQLException {
		
		String sql="select * "
				+  "from Bornette "
				+  "where adresse_station='"+adresseStation+"' "
				+  "and id_Velo is null and etat_bornette='OK'";
		
		//System.out.println("INFO : Requete"+ sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		return result.next();
	}

	public static void rendreVelo(String idBornette, String idClient,
			String idLocation, Boolean isAbonne) throws SQLException {
		
		Connection connection = DbConnection.getInstance();
		connection.setAutoCommit(false);
		String idVelo;
		
		//on recherche l'idVélo
		String sqlIdVelo = "Select id_velo from location where id_location = '"+idLocation+"'";
		//System.out.println("INFO : requete : "+ sqlIdVelo);
		
		PreparedStatement prepareIdVelo = connection.prepareStatement(sqlIdVelo);
		
		ResultSet resultIdVelo = prepareIdVelo.executeQuery();
		
		resultIdVelo.next();
		idVelo = resultIdVelo.getString("id_velo");
		

		String sqlVeloBornette="update Bornette set id_velo='"+idVelo+"' where id_bornette='"+idBornette +"'";
		String sqlLocation="update location set DATE_HEURE_FIN_LOCATION=sysdate,ID_BORNETTE_FIN='"+idBornette+"' where id_location='"+idLocation +"'";
		
		//System.out.println("INFO : requete : "+ sqlVeloBornette);
		//System.out.println("INFO : requete : "+ sqlLocation);
		
		//-----On attache un Vélo à bornette------------
		PreparedStatement prepareVeloBornette = connection.prepareStatement(sqlVeloBornette);
		
		prepareIdVelo.executeUpdate(sqlVeloBornette);
		
		//-----On insert dans location une date de fin-----------
		PreparedStatement prepareLocation = connection.prepareStatement(sqlLocation);
		
		prepareLocation.executeUpdate(sqlLocation);
		
		
		//si il n'est pas abonné on le supprime de la base
		if (!isAbonne){
			String sqlSupprNonAbonne="delete from nonAbonne where id_client ='"+idClient+"'";
			PreparedStatement prepareSupprClient = connection.prepareStatement(sqlSupprNonAbonne);	
			
			prepareSupprClient.executeUpdate();
		}
		
		
		connection.commit();
		connection.setAutoCommit(true);
		
		prepareIdVelo.close();
		prepareLocation.close();
	}

	public static String getPremiereBorneDispo(String adresseStation) throws SQLException {
		// Renvoie l'id de la première borne dispo
		
		String sql = "select id_bornette from Bornette where adresse_station='"+adresseStation+"' and id_Velo is null and etat_bornette='OK'";
		
		System.out.println("INFO : Requete : "+sql);
		
		Connection connection = DbConnection.getInstance();
		PreparedStatement prepare = connection.prepareStatement(sql);
		
		ResultSet result = prepare.executeQuery();
		
		String idBornette;
		
		if (result.next()){
			
			idBornette= result.getString("id_bornette");
			
		}
		else{
			idBornette = null;
		}
		
		
		return idBornette;
	}
	
	public static void creerReservation(String idResa, String numAbonne,String adresseStation, Date today, String dateDebutReservation,	String dateFinReservation, String jour_Choisi) throws SQLException {
		Connection connection = DbConnection.getInstance();
		connection.setAutoCommit(false);
		String sqlInsertReservation ="insert into Reservation values('"+idResa+"','"+numAbonne+"','"+adresseStation+"',sysdate,to_date( '"+dateDebutReservation+"' , 'yyyy/mm/dd' ),to_date( '"+dateFinReservation+"' , 'yyyy/mm/dd' ),'"+jour_Choisi+"')";
		System.out.println("INFO : requete : " +sqlInsertReservation);
		PreparedStatement prepareInsertReservation = connection.prepareStatement(sqlInsertReservation);
		prepareInsertReservation.executeUpdate();
		connection.commit();
		connection.setAutoCommit(true);
	}

	 //Fonction qui permet de renouveler un abonnement
    public static void renouvelerAbonnement(String codeSecret) throws SQLException{


        String sql1="update abonne set DATE_DEBUT_ABONNEMENT = add_months(DATE_DEBUT_ABONNEMENT,12)where code_secret='"+codeSecret+"'";

        String sql="Select DATE_DEBUT_ABONNEMENT from abonne where code_secret='"+codeSecret+"'";
        Connection connection = DbConnection.getInstance();
        PreparedStatement prepare = connection.prepareStatement(sql);
        PreparedStatement prepare1 = connection.prepareStatement(sql1);

        ResultSet result = prepare.executeQuery();
        ResultSet result1 = prepare1.executeQuery();


        result.next();
    
    //System.out.println(result.getDate("DATE_DEBUT_ABONNEMENT"));

    }







    //Inserer un nouveau Client
    public static String  nouveauClient(String id_Client, String id_remise) throws SQLException{

        String sql ="insert into client values ('"+id_Client+"','"+id_remise+"')";


        Connection connection = DbConnection.getInstance();
        PreparedStatement prepare = connection.prepareStatement(sql);


        prepare.executeUpdate();

        return id_Client;

    }

    //Fonction qui permet d'inserer un nouveau abonne
    public static void  nouveauAbonne(    String CODE_SECRET ,String ID_CLIENT, String NOM_CLIENT ,
            String PRENOM_CLIENT ,String  DATE_NAISSANCE , String SEXE_CLIENT ,
            String ADRESSE_CLIENT,int CB_CLIENT ,
            String DATE_DEBUT_ABONNEMENT ) throws SQLException{

        String sql ="insert into abonne values ('"+CODE_SECRET+"','"+ID_CLIENT+"','"+NOM_CLIENT+"','"+PRENOM_CLIENT+"','"+DATE_NAISSANCE+"','"+SEXE_CLIENT+"','"+ADRESSE_CLIENT+"','"+CB_CLIENT+"','"+DATE_DEBUT_ABONNEMENT+"')";


        Connection connection = DbConnection.getInstance();
        PreparedStatement prepare = connection.prepareStatement(sql);


        prepare.executeUpdate();



    }


    //Retourne la date actuelle
    public static String  dateActuelle(){
        Date actuelle = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dat = dateFormat.format(actuelle);


        return dat;
    }

    
    
    //Fonction qui permet de retouner une valeure booleene pour tester si le renouvelemnt d abonnement est superieur à 11 mois
    public static Boolean  dateAbonnement(String codeSecret) throws SQLException{

        String sql="Select DATE_DEBUT_ABONNEMENT from abonne where code_secret='"+codeSecret+"'";


        Connection connection = DbConnection.getInstance();
        PreparedStatement prepare = connection.prepareStatement(sql);

        ResultSet result = prepare.executeQuery();

        result.next();
        
        Calendar cal = Calendar.getInstance();
        
        Date d=new Date();

        cal.setTime(result.getDate("DATE_DEBUT_ABONNEMENT"));
        cal.add(cal.HOUR_OF_DAY, 8030);//date de debut d'abonnement +11 mois
        System.out.println(cal.getTime());
        if (d.compareTo(cal.getTime()) == -1) { //si inferieur a 11 mois

            return false;
        }
        else if(d.compareTo(cal.getTime()) == 1) {// si superieur a 11 mois
            return true;
            
        }
    
            return true;
        

    }

 


}




