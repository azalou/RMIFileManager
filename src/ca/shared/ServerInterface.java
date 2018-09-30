package ca.shared;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	
	boolean verify(String login, String password) throws RemoteException;
	public boolean newlog(String login, String password) throws RemoteException;
}
