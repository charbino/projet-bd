package client;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

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
			
			break;
		case 3:
			
			break;
		default: 
			System.out.println("Choix impossible veuillez recommencer");
			choixMenu();
		}
		
	}

	private void louerUnVelo() {
		
		Boolean isAbonne= false;
		
		//Abonné ? non Abonné ? 
		Scanner scAb = new Scanner(System.in);
		int choixAB;
		String numAbonne = null;
		
		System.out.println("Etes-vous abonné ? ");
		do{
			
			System.out.println("1- Oui");
			System.out.println("2- Non");
			choixAB = scAb.nextInt();
		}
		while(choixAB!=1 && choixAB!=2);
		
		
		if(choixAB==1){
			isAbonne= true;
			
			//verification des identifiants
			numAbonne = verifierAbonne();
			if (numAbonne.equals("X-X")){
				System.out.println("Erreur sur l'abonné");
				this.choixMenu();
			}
			
		}
		else if(choixAB==2){
			isAbonne= false;
			
		}
		
		
		//on recupère les bornes disponible
		Scanner sc = new Scanner(System.in);
		System.out.println("+-------------------------------+");
		System.out.println("System : adresse de la station ? ");
		String adresseStation = sc.nextLine();
		System.out.println("+-------------------------------+");
		
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
					
					idCLient = modeleClient.chercherCLient(numAbonne);
				}else{
					//si il n'est pas abboné on l'ajoute dans la base
					//1 on genere un code secret client
					int Min = 10000000;
					int Max = 99999999;
					
					int nombreAleatoireCodeSecretNonAbonne = Min + (int)(Math.random() * ((Max - Min) + 1));
				
					//verifie que ce code n'exsite déja pas 
					if( ! modeleClient.codeSecretNonAbonneUnique(nombreAleatoireCodeSecretNonAbonne) ){
						System.out.println("Erreur L'ID n'est pas unique");
					}

					
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

		System.out.println("Votre numéro abonné :");
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
}