package ca.rmiFMServer;

import java.rmi.ConnectException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.SecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import ca.shared.ServerInterface; 


public class FMServer implements ServerInterface {
	
	public static void main(String[] args) {
		FMServer server = new FMServer();
		server.run();
	}

	public FMServer() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
			//System.setProperty("java.security.policy","file:/home/azalou/Documents/eclipse-workspace/RMIFileManager//policy");
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("fmserver", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	
	@Override
	public boolean verify(String login, String password) throws RemoteException {
		boolean clientVerified = false;
		String tempUser = "";
		String tempPassWor = "";
		Scanner lecteur;

		try {
			lecteur = new Scanner(new File("logins.txt"));
			lecteur.useDelimiter("[,\n]");

			while (lecteur.hasNext() ) { 
				tempUser = lecteur.next();
				tempPassWor = lecteur.next();

				if (tempUser.trim().equals(login.trim())){ 
						//&& 
					if (tempPassWor.trim().equals(password.trim())) {
					clientVerified = true;
					}else{
						clientVerified = false;
					}
				}
			}
			lecteur.close();
		} catch (Exception e) {
			System.out.println("erreur: " + e.getMessage());
		}
		return clientVerified;
	}
	@Override
	public boolean newlog(String login, String password) throws RemoteException {
		Scanner lecteur;
		boolean isNewUserCreated = false;
		try {
			lecteur = new Scanner(new File("logins.txt"));
			lecteur.useDelimiter("[,\n]");
			String tempUser = "";
			String tempPasswor = "";
			
			//verifie que le nom d'utilisateur n'existe pas déjà)
			while (lecteur.hasNext() ) { 
				tempUser = lecteur.next();
				tempPasswor = lecteur.next();

				if (tempUser.trim().equals(login.trim())){ 
					System.out.println("L'utilisateur existe déjà sosdsjfls");
					return isNewUserCreated;	
				}
			}
			
			FileWriter fl = null;
			String nn = login + "," + password ;
			fl = new FileWriter("logins.txt", true);
			fl.write(nn);
			fl.close();
			isNewUserCreated = true;
			return isNewUserCreated;
			} catch (Exception e) {
				System.out.println("erreur était: " + e.getMessage());
				return isNewUserCreated;
			} 
		}
	}
