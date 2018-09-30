package ca.rmiFMClient;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import ca.shared.ServerInterface;

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
//		boolean quitApp = false;
		/*while (!quitApp) {
			
		}*/
		Scanner scanInput = new Scanner(System.in);
		String newUser = "";
		if (stub != null) {
			if (checkLogin()) {
				String command = "";
				System.out.println("succes");
				boolean interactConsole = true;
				while (interactConsole) {
					System.out.println(">");
					command = scanInput.nextLine();
					switch (command) {
					case "exit": interactConsole = false;
					break;
					default: System.out.println("invalid command");
					break;
					}
				}
			}
			else {
				System.out.println("Mauvais nom d'utiliateur ou mot de passe. Create new user (yes/no)?");
				newUser = scanInput.nextLine();
				if (newUser.trim().equals("yes")) {
					createUser();
				}
			}		
		}
		scanInput.close();
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("fmserver");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
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
		//scanner.close();
		return Validation;
	}
	
	private boolean createUser() {
		boolean isUserCreated = false;
		System.out.print("Entrer le nom d'utilisateur : ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.print("Entrer le mot de passe voulu : ");
		String username = scanner.nextLine();
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

