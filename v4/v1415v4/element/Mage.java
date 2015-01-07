package element;

import interaction.Actions;
import interaction.Capacite;
import interaction.Deplacements;
import interfaceGraphique.VueElement;

import java.rmi.RemoteException;
import java.util.Hashtable;

import utilitaires.Calculs;

public class Mage extends Personnage implements Capacite{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Représente l'état de création d'un potion
	private int nbPas;
	//Temps de création d'une potion
	private static final int tempsCreationPotion = 10;
	
	
	public Mage() {
		super("Mage", 50, 20);
		// TODO Auto-generated constructor stub
		
		nbPas = 0;
	}
	
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
        
        nbPas++;
        
        if(nbPas == tempsCreationPotion){
        	capacite(ve,actions);
        	nbPas = 0;
        }
	}
	
	public void capacite(VueElement ve, Actions actions) throws RemoteException{
		actions.creerPotion();
	}

}
