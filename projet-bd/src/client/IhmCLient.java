package client;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

//Class qui gère l'ihm du client 
public class IhmCLient {

	public static void main(String[] args) {
		
		System.out.println("--------------------------------------------");
		System.out.println("Bienvenue dans l'application dédié au client");
		System.out.println("--------------------------------------------\n");
		
		IhmCLient Ihm = new IhmCLient();
		
		Ihm.choixMenu();
		
	}
	
	public void afficherMenu(){
		
	    
		System.out.println("--Menu : --");
		System.out.println("1-- Louer un Vélo");
		System.out.println("2-- S'abonner");
		System.out.println("3-- Rendre un vélo");
		System.out.println("4-- Réserver un vélo");
		System.out.println();
		System.out.println("100-- Quitter");
		
	}
	
	//en fonction du choix 
	public void choixMenu(){
		afficherMenu();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Choix ? ");
		int choix = sc.nextInt();
		
		switch(choix){
		case 1:
			louerUnVelo();
			break;
		case 2:
			 abonner();
			break;
		case 3:
			rendreVelo();
			
			break;
		case 4:
			reserverUnVelo();
			
			break;
		default: 
			System.out.println("Choix impossible veuillez recommencer");
			choixMenu();
		}
		
	}


	private void louerUnVelo() {
		
		Boolean isAbonne= false;
		
		//Abonné ? non Abonné ? 
		int choixAB;
		String numAbonne = null;
		
		isAbonne = demandeTypeClient();
		
		if(isAbonne){
			isAbonne= true;
			
			//verification des identifiants
			numAbonne = verifierAbonne();
			if (numAbonne.equals("X-X")){
				System.out.println("Erreur sur l'abonné");
			}		
		}
		
		String adresseStation = systemeChoisirAdresseStation();
		
		//on affiche les bornes disponibles (= vélo disponible)
		try {
			HashMap borneDispo = new HashMap();
			borneDispo = modeleClient.getVeloLibreStation(adresseStation);
			System.out.println("");
			System.out.println("Liste des vélo disponible pour la station "+adresseStation+" :" );
			
			
			int nombreVeloDisonible = borneDispo.size();
			
			if (nombreVeloDisonible==0){
				System.out.println("Il n'y a pas de vélo disponible");
				
			}else{
				for(int i = 1; i <= nombreVeloDisonible; ++i){
					System.out.println(i+"- "+ borneDispo.get(i));
				}	
				
				//choix du vélo disponible
				
				Scanner scannerChoixVeloLibre = new Scanner(System.in);
				int choixVeloLibre;
				
				do{
					System.out.println("Quel numéro de vélo ? ");
					choixVeloLibre = scannerChoixVeloLibre.nextInt();
				}
				while(choixVeloLibre<0 || choixVeloLibre>nombreVeloDisonible );
				
				 String idBornetteChoisit = borneDispo.get(choixVeloLibre).toString();
				 String idCLient;
				
				if(isAbonne){
					
					idCLient = modeleClient.chercherCLientAbonne(numAbonne);
				}else{
					boolean idUnique ;
					int nombreAleatoireCodeSecretNonAbonne;
					do{
						idUnique=true;
						
						//si il n'est pas abboné on l'ajoute dans la base
						//1 on genere un code secret client
						int Min = 10000000;
						int Max = 99999999;
						
						nombreAleatoireCodeSecretNonAbonne = Min + (int)(Math.random() * ((Max - Min) + 1));
					
						//verifie que ce code n'exsite déja pas 
						boolean codeSecretNonAbonneUnique = modeleClient.codeSecretNonAbonneUnique(nombreAleatoireCodeSecretNonAbonne);
						if( !codeSecretNonAbonneUnique){
							System.out.println("Erreur L'ID n'est pas unique");
							idUnique=false;
						}
					}while(!idUnique);

					
					//2 on 	L'insert
					Scanner scCBCLient = new Scanner(System.in);
					System.out.println("+-------------------------------+");
					System.out.println("System : CB du client ? ");
					int CBClient = scCBCLient.nextInt();
					System.out.println("+-------------------------------+");
					
					idCLient = modeleClient.insererNonAbonne(nombreAleatoireCodeSecretNonAbonne,CBClient);
					
					System.out.println("------------------------------------------");
					System.out.println("Voici vote code secret : "+nombreAleatoireCodeSecretNonAbonne);
					System.out.println("\nAttention ce code vous servira à rendre le vélo");
					System.out.println("------------------------------------------");
					
				}
				
				
				//on ajoute la location et on supprime le vélo dans bornette
				modeleClient.creerLocation(idCLient,idBornetteChoisit,adresseStation);
				System.out.println("Vous pouvez prendre votre vélo");
				

				//gestion de la date de retour
				Calendar cal = Calendar.getInstance();
			    cal.setTime(new Date()); 
			    cal.add(Calendar.HOUR_OF_DAY, 12);
				
			    
				System.out.println("Vous devez le rendre avant : "+ cal.getTime());
				
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//verifie que l'abonné est bien dans la bd
	private String verifierAbonne() {

		System.out.println("Votre code secret abonné :");
		Scanner scNumAb = new Scanner(System.in);
		String numAB;
		Boolean abonnementValide=false;
		numAB = scNumAb.nextLine();
		
		//on verifie que l'abonnement existe
		try {
			if (modeleClient.abonnementExiste(numAB)){
			
				System.out.println("Abonnement existant");
				
				//on verifie la validité de l'abonnement
				if (modeleClient.abonnementValide(numAB)){
					System.out.println("Abonnement valide");
					abonnementValide=true;
					
				}
				else{
					System.out.println("Erreur l'abonnement n'est plus valide, veuillez le renouveller");
					abonnementValide=false;
				}
			}
			else{
				System.out.println("Erreur l'abonnement existe pas");
				abonnementValide=false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (abonnementValide){
		}else{
			numAB="X-X";
		}
		
		return numAB;

	}
	
	private void rendreVelo() {
		
		//le systeme demande l'adresse de la station
		String adresseStation = systemeChoisirAdresseStation();
		
		//on verifie qu'il y a de la place dans la station choisit
		try {
			if(!modeleClient.placeDispoStation(adresseStation)){
				
				System.out.println("Il n'y a pas de place disponible");
				
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String idClient = null;
		
		//on demande si c'est un abonné ou un client normal
		Boolean isAbonne = demandeTypeClient();
		if (isAbonne) {

					
			//on recumère l'id du client abonné
			try {
				String codeSecretAbonne;
				Scanner scNumAb;
				Boolean isTrue = false;
				do {
					//on lui demande son codeSecret
					System.out.println("Votre code Secret abonné (ou tapez q pour quitter) :");
					scNumAb = new Scanner(System.in);
					codeSecretAbonne = scNumAb.nextLine();
					
					if(codeSecretAbonne.equals("q")){
						//this.choixMenu();
					}
					else{
						isTrue = modeleClient.abonnementExiste(codeSecretAbonne);
						if (!isTrue){
							System.out.println("Le code saisit n'est pas valide veuillez recommencer");
						}
					}
					
				} while (!isTrue);
					
				//on recupère l'id du client
					idClient= modeleClient.chercherCLientAbonne(codeSecretAbonne);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
			//on demande le codeSecret
			String codeSecretNonAbonne;
			Scanner scNumNonAb;
			Boolean isTrue = false;
			
			do {
				//on lui demande son codeSecret
				System.out.println("Votre code de location (ou tapez q pour quitter) :");
				scNumNonAb = new Scanner(System.in);
				codeSecretNonAbonne = scNumNonAb.nextLine();
				
				if(codeSecretNonAbonne.equals("q")){
					//this.choixMenu();
				}
				else{
					try {
						isTrue = modeleClient.nonAbonneExiste(codeSecretNonAbonne);
						if (!isTrue){
							System.out.println("Le code saisit n'est pas valide veuillez recommencer");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} while (!isTrue);
			
			//on recupère l'id du client non abonné
			try {
				idClient = modeleClient.chercherCLientNonAbonne(codeSecretNonAbonne);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		//--------On retrouve la location
		try {
			String idLocation = modeleClient.retrouverLocation(idClient);
			
			if(idLocation.equals("null")){
				System.out.println("Erreur la location n'a pas été retrouvé ");
				System.out.println("Warning : L'application redemarre");
				//this.choixMenu();
			}
			
			//on lui impose une borne et rend le vélo
			String idBorneDispo;
			
			idBorneDispo = modeleClient.getPremiereBorneDispo(adresseStation);
		
			
			if (idBorneDispo==null){
				System.out.println("Erreur : Il n'y a pas de vélo disponible");
				
			}else{
				System.out.println("Metter le vélo sur la borne : "+idBorneDispo);
				System.out.println("Votre Vélo à bien été rendu");
				
				//---RAF : Il faut calculer combien coute la location
				
				modeleClient.rendreVelo(idBorneDispo,idClient,idLocation,isAbonne);
				
				
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void reserverUnVelo() {
		
		Boolean isAbonne= false;
		
		//Abonné ? non Abonné ? 		
		String numAbonne = null;

			
			//verification des identifiants
			numAbonne = verifierAbonne();
			if (numAbonne.equals("X-X")){
				System.out.println("Erreur sur l'abonné");
				this.choixMenu();
			}		


		
			
		try {


				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date()); 
				Date today = cal.getTime();
				// A SUPPRIMER cal.add(Calendar.HOUR_OF_DAY, 0);
			
			

		
				//choix du vélo disponible
				
				Scanner scannerChoix = new Scanner(System.in);
				String dateDebutReservation;
				String dateFinReservation;
				String rep_jour_Choisi;
				String jour_Choisi="Tous_les_jours";
				System.out.println("Veuillez tapez la date de debut de réservation au format aaaa/mm/jj");

				dateDebutReservation = scannerChoix.nextLine();
				System.out.println("Veuillez tapez la date de fin de réservation au format aaaa/mm/jj");
				dateFinReservation= scannerChoix.nextLine();
				System.out.println("Choisir le jour de la semaine où effectuer la réservation");
				System.out.println("L--:Lundi \n Ma--Mardi \n Me--Mercredi \n J--Jeudi \n V--Vendredi \n S--Samedi \n D--Dimanche \n T-- Tous_les_jours");
				rep_jour_Choisi = 	scannerChoix.nextLine();
				switch(rep_jour_Choisi){
				case "L":
					jour_Choisi="Lundi";
					break;
				case "Ma":
					jour_Choisi="Mardi";
					break;
				case "Me":
					jour_Choisi="Mercredi";			
					break;
				case "J":
					jour_Choisi="Jeudi";
				break;
				case "V":
					jour_Choisi="Vendredi";
				break;
				case "S":
					jour_Choisi="Samedi";
				break;
				case "D":
					jour_Choisi="Dimanche";
				break;
				case "T":
					jour_Choisi="Tous_les_jours";
				break;
				default: 
					System.out.println("Choix impossible veuillez recommencer");
					jour_Choisi = 	scannerChoix.nextLine();
				}
				
				String adresseStation = systemeChoisirAdresseStation();
				UUID idResarandom = UUID.randomUUID();
				String idResa = idResarandom.toString();
				//on ajoute la location et on supprime le vélo dans bornette
				modeleClient.creerReservation(idResa,numAbonne,adresseStation,today,dateDebutReservation,dateFinReservation,jour_Choisi);
				System.out.println("Votre réservation est validée : \n Numéro réservation : "+idResa+" \n Date debut réservation : "+dateDebutReservation+ " \n Date fin réservation : "+dateFinReservation+" \n Jour choisi : "+jour_Choisi+"");
				

				scannerChoix.close();
				
			    
				
		
		
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	 private void abonner()  {
		    
	        Scanner scAb = new Scanner(System.in);
	        int choixAB;

	        do{

	            System.out.println("1- Renouvellement abonnement ? ");
	            System.out.println("2- Nouveau client? ");
	            System.out.println("3- annuler ");
	            choixAB = scAb.nextInt();
	        }
	        while(choixAB!=1 && choixAB!=2 && choixAB!=3);
	        try {
	            
	            if(choixAB==1){
	                System.out.println("Veuillez saisir votre code secret ");
	                String code_Sec = scAb.next();
	                
	                if (modeleClient.abonnementExiste(code_Sec)){
	                        if(modeleClient.dateAbonnement(code_Sec)){//Si la date de renouvelement est superieur à 11 mois
	                            modeleClient.renouvelerAbonnement(code_Sec);
	                         }
	                        else{
	                            System.out.println("Vous ne pouvez pas renouveler votre abonnement ");
	                        }
	                    
	                    System.out.println("Au revoooooooooir <3 <3 <3");
	                }

	                else {

	                    System.exit(-1);
	                }

	            }
	            else if (choixAB==2){
	                UUID idUniqueClient = UUID.randomUUID();//genere un id_Client unique
	                idUniqueClient.toString();
	                UUID idUniqueAbonne = UUID.randomUUID();
	                idUniqueAbonne.toString();

	                System.out.println("Veuillez remplir le formulaire si dessous ");
	                String id_Cl=modeleClient.nouveauClient(idUniqueClient.toString(),"");

	                System.out.println("Saisir nom");

	                String nom = scAb.next();
	                System.out.println("Saisir Prenom");

	                String prenom = scAb.next();
	                System.out.println("Saisir date de naissance");

	                String date_Naiss = scAb.next();
	                System.out.println("Saisir sexe");

	                String sexe = scAb.next();

	                System.out.println("Saisir adresse");

	                String adresse = scAb.next();

	                System.out.println("Saisir num carte bancaire");

	                int cb = scAb.nextInt();

	                String cs;

	                do {
	                    System.out.println("Saisir un code secret");
	                    cs = scAb.next();
	                    boolean s=modeleClient.abonnementExiste(cs);
	                    System.out.println("prob"+s);

	                }

	                while (modeleClient.abonnementExiste(cs)==true);

	                Calendar cal = Calendar.getInstance();
	                  cal.setTime(new Date());
	                  cal.add(Calendar.HOUR_OF_DAY, 8760);//ajouter un an à la date actuelle
	                 Date d=cal.getTime();
	                modeleClient.nouveauAbonne(cs, id_Cl, nom, prenom, date_Naiss, sexe, adresse, cb, modeleClient.dateActuelle());    
	                System.out.println("Votre abonnement est valable jusqu'au : "+d);
	                System.out.println("Au revoooooooooir <3 <3 <3");
	                System.exit(-1);
	            }

	            else{
	                System.out.println("Au revoooooooooir <3 <3 <3");
	                System.exit(-1);
	                

	            }
	        } catch (SQLException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }





	    }


	
	private String systemeChoisirAdresseStation() {
		//on recupère les bornes disponible
		Scanner sc = new Scanner(System.in);
		System.out.println("+-------------------------------+");
		System.out.println("System : adresse de la station ? ");
		String adresseStation = sc.nextLine();
		System.out.println("+-------------------------------+");
		
		return adresseStation;
	}
	
	private Boolean demandeTypeClient() {
		//true si abonne
		Scanner scAb = new Scanner(System.in);
		int choixAB;
		System.out.println("Etes-vous abonné ? ");
		do{
			
			System.out.println("1- Oui");
			System.out.println("2- Non");
			choixAB = scAb.nextInt();
		}
		while(choixAB!=1 && choixAB!=2);

		return (choixAB == 1);
	}
}