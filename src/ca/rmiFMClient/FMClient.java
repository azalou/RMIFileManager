package ca.rmiFMClient;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import ca.shared.ServerInterface;
import ca.shared.WorkFile;

public class FMClient {
	public static void main(String[] args) {
		String distantHostname = "127.0.0.1";

		if (args.length > 0) {
			distantHostname = args[0];
		}

		FMClient client = new FMClient(distantHostname);
		client.run();
	}

	private ServerInterface stub = null;

	public FMClient(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		if (distantServerHostname != null) {
			stub = loadServerStub(distantServerHostname);
		}
	}

	private void run() {
		Scanner scanInput = new Scanner(System.in);
		if (stub != null) {
			if (checkLogin()) {
				String command = "";
				System.out.println("succes");
				boolean interactConsole = true;
				while (interactConsole) {
					System.out.println(">");
					command = scanInput.nextLine();
					switch (command) {
					case "exit":
						interactConsole = false;
						break;
					case "list":
						getListOfFiles();
						break;
					default:
						System.out.println("invalid command");
						break;
					}
				}
			} else {
				System.out.println("Mauvais nom d'utiliateur ou mot de passe. Create new user (yes/no)?");

				if (scanInput.nextLine().trim().equals("yes")) {
					createUser();
					System.out.println("user created");
				}
			}
		}
		scanInput.close();
	}

	private void getListOfFiles() {
		try {
			ArrayList<WorkFile> listFiles = stub.list();
			for (int count = 0; count < listFiles.size(); count++) {
				System.out.println(listFiles.get(count).getName());
			}
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("fmserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage() + "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private boolean checkLogin() {
		boolean Validation = false;
		System.out.print("Entrer le nom d'utilisateur : ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String username;
		String password;
		username = scanner.nextLine();
		System.out.print("Entrer le mot de passe : ");
		password = scanner.nextLine();
		try {
			Validation = stub.verify(username, password);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
			e.printStackTrace();
		}
		// scanner.close();
		return Validation;
	}

	private boolean createUser() {
		boolean isUserCreated = false;

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.print("Entrer le nom d'utilisateur : ");
		String username = scanner.nextLine();
		System.out.print("Entrer le mot de passe voulu : ");
		String password = scanner.nextLine();

		try {
			isUserCreated = stub.newlog(username, password);
			return isUserCreated;
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
			return isUserCreated;
		}

	}

}
