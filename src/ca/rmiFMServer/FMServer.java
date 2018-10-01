package ca.rmiFMServer;

import java.rmi.ConnectException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
	private static final String CONF_DIR = "configDir";
	private final static String user_logins = "logins.txt";

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
		} catch (ConnectException e1) {
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur1: " + e1.getMessage());
		} catch (Exception e2) {
			System.err.println("Erreur2: " + e2.getMessage());
		}
	}

	@Override
	public boolean verify(String login, String password) throws RemoteException {
		boolean clientVerified = false;

		try {
			String tempUser = "";
			String tempPassWor = "";
			Scanner lecteur;
			lecteur = new Scanner(new File(CONF_DIR + File.separator + user_logins));
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
			System.out.println("FileNotFoundException: " + e.getMessage() + " On cree le fichier de login");
			new File(CONF_DIR).mkdirs();
			File userLogins = new File(CONF_DIR + File.separator + user_logins);
			if (!userLogins.exists())
				try {
					userLogins.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage() + " erreur dans verify");
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
			lecteur = new Scanner(new File(CONF_DIR + File.separator + user_logins));
			lecteur.useDelimiter("[,\n]");
			// verifie que le nom d'utilisateur n'existe pas déjà)
			while (lecteur.hasNext()) {
				tempUser = lecteur.next();
				tempPasswor = lecteur.next();
				
				if (tempUser.trim().equals(login.trim())) {	
					
					System.out.println("L'utilisateur existe déjà");
					return isNewUserCreated;
				}
			}

			FileWriter fl = null;
			String nn = login + "," + password;
			fl = new FileWriter(CONF_DIR + File.separator + user_logins, true);
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

	@Override
	public String createIDclient() throws RemoteException {
		return clientManager.createClientID();
	}
}
