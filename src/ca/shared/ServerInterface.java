package ca.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

	boolean verify(String login, String password) throws RemoteException;

	public boolean newlog(String login, String password) throws RemoteException;

	WorkFile lock(String name, String clientID, String checksum) throws RemoteException;

	ArrayList<WorkFile> list() throws RemoteException;

	Byte[] get(String fic, String chks);
}
