package client;

import java.sql.SQLException;
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
		
		//Abonné ? non Abonné ? 
		Scanner scAb = new Scanner(System.in);
		int choixAB;
		System.out.println("Etes-vous abonné ? ");
		do{
			
			System.out.println("1- Oui");
			System.out.println("2- Non");
			choixAB = scAb.nextInt();
		}
		while(choixAB!=1 && choixAB!=2);
		
		
		if(choixAB==1){
			//verification des identifiants
			verifierAbonne();
		}
		else if(choixAB==2){
			
		}
		
		
		//on recupère les bornes disponible
		Scanner sc = new Scanner(System.in);
		System.out.println("+-------------------------------+");
		System.out.println("System : adresse de la station ? ");
		String adresseStation = sc.nextLine();
		System.out.println("+-------------------------------+");
		
		//on affiche les bornes disponibles
		try {
			HashMap borneDispo = new HashMap();
			borneDispo = modeleClient.getVeloLibreStation(adresseStation);
			System.out.println("");
			System.out.println("Liste des vélo disponible pour la station "+adresseStation+" :" );
			
			for(int i = 1; i <= borneDispo.size(); ++i){
				System.out.println(i+"- "+ borneDispo.get(i));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	
	




	//verifie 
	private void verifierAbonne() {

		System.out.println("Votre numéro abonné :");
		Scanner scNumAb = new Scanner(System.in);
		String numAB;
		numAB = scNumAb.nextLine();
		
		//on verifie que l'abonnement existe
		try {
			if (modeleClient.abonnementExiste(numAB)){
			
				System.out.println("ouii");
			}
			else{
				System.out.println("non");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}