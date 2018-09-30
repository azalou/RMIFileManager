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
import java.util.ArrayList;
import java.util.Scanner;

import ca.shared.ClientManager;
import ca.shared.FMFileManager;
import ca.shared.ServerInterface;
import ca.shared.WorkFile;

public class FMServer implements ServerInterface {
	private final static FMFileManager fileManager = new FMFileManager();
	private final static ClientManager clientManager = new ClientManager();

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
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("fmserver", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	@Override
	public boolean verify(String login, String password) throws RemoteException {
		boolean clientVerified = false;

		try {
			String tempUser = "";
			String tempPassWor = "";
			Scanner lecteur;
			lecteur = new Scanner(new File("logins.txt"));
			lecteur.useDelimiter("[,\n]");

			while (lecteur.hasNext()) {
				tempUser = lecteur.next();
				tempPassWor = lecteur.next();

				if (tempUser.trim().equals(login.trim())) {
					if (tempPassWor.trim().equals(password.trim())) {
						clientVerified = true;
					} else {
						clientVerified = false;
					}
				}
			}
			lecteur.close();
		} catch (FileNotFoundException e) {
			System.out.println("erreur: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("erreur: " + e.getMessage());
		}
		return clientVerified;
	}

	@SuppressWarnings("resource")
	@Override
	public boolean newlog(String login, String password) throws RemoteException {
		Scanner lecteur;
		boolean isNewUserCreated = false;
		String tempUser = "";
		String tempPasswor = "";
		try {
			lecteur = new Scanner(new File("logins.txt"));
			lecteur.useDelimiter("[,\n]");
			// verifie que le nom d'utilisateur n'existe pas déjà)
			while (lecteur.hasNext()) {
				tempUser = lecteur.next();
				tempPasswor = lecteur.next();
				
				if (tempUser.trim().equals( login.trim())) {
					System.out.println("L'utilisateur existe déjà");
					return isNewUserCreated;
				}
			}

			FileWriter fl = null;
			String nn = login + "," + password;
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

	@Override
	public WorkFile lock(String name, String clientID, String checksum) throws RemoteException {
		return fileManager.lock(name, clientID, checksum);
	}

	@Override
	public ArrayList<WorkFile> list() throws RemoteException {
		return fileManager.listFiles();
	}
	
	@Override
	public String get(String fic, String chks) {
		if (fileManager.isSameChecksum(fic, chks)) {
			return null;
		} else {
			return fileManager.readFile(fic);
		}
	}

}
