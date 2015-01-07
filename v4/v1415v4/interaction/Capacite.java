package interaction;

import java.rmi.RemoteException;

import interfaceGraphique.VueElement;

public interface Capacite {
	
	public void capacite(VueElement ve, Actions actions) throws RemoteException;

}
