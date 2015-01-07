package element;

import interaction.Actions;
import interaction.Capacite;
import interaction.Deplacements;
import interfaceGraphique.VueElement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;



import controle.IConsole;
import utilitaires.Calculs;


public class Roi extends Personnage implements Capacite {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int temps_cool = 4;
	private static int force_bonus = 50;
	private int cooldown;

	public Roi () {
		super("Roi", 10, 50);
		cooldown=0;
	}
	
	@Override
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
        Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
        Deplacements deplacements = new Deplacements(ve,voisins);
        
        if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
        	parler("J'erre...", ve);
        	deplacements.seDirigerVers(0); //errer
            
        } else {
			VueElement cible = Calculs.chercherElementProche(ve, voisins);
			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());
			
			int refPlusProche = cible.getRef();
			Element elemPlusProche = cible.getControleur().getElement();
			
			// dans la meme equipe ?
			boolean memeEquipe = false;
			
			if(elemPlusProche instanceof Personnage) {
				memeEquipe = (this.getLeader() != -1 && this.getLeader() == ((Personnage) elemPlusProche).getLeader()) || // meme leader
						this.getLeader() == refPlusProche || // cible est le leader de this
						((Personnage) elemPlusProche).getLeader() == refRMI; // this est le leader de cible
			}
			
			if(distPlusProche <= 2) { // si suffisamment proches
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage
					parler("Je ramasse une potion", ve);
					actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
					
				} else { // personnage
					if(!memeEquipe) { // duel seulement si pas dans la meme equipe (pas de coup d'etat possible dans ce cas)
						// duel
						parler("Je fais un duel avec " + refPlusProche, ve);
						actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
						if(actions.getResult())capacite(ve,actions);
					} else {
			        	parler("J'erre...", ve);
			        	deplacements.seDirigerVers(0); // errer
					}
				}
			} else { // si voisins, mais plus eloignes
				if(!memeEquipe) { // potion ou enemmi 
					// je vais vers le plus proche
		        	parler("Je vais vers mon voisin " + refPlusProche, ve);
		        	deplacements.seDirigerVers(refPlusProche);
		        	
				} else {
		        	parler("J'erre...", ve);
		        	deplacements.seDirigerVers(0); // errer
				}
			}
        }
        
		if(cooldown != 0){
			cooldown--;
			return;
		}
	}
	
	/**
	 * La fonction capacite est la capacité spéciale du roi. 
	 * Elle applique un buff de force à son équipe.
	 * Elle est appelée quand le roi vient de faire un duel.
	 * @param ve
	 * @throws RemoteException
	 */
	public void capacite(VueElement ve, Actions actions) throws RemoteException{
		
		ArrayList<Integer> list = this.getEquipe();
		
		actions.courage(ve, actions, force_bonus, list);
		
		cooldown = temps_cool;
			
	}
}
