package interaction;

import interfaceGraphique.VueElement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import serveur.Arene;
import serveur.IArene;
import controle.Console;
import controle.IConsole;
import element.Element;
import element.SuperPotion;

/**
 * Created by moi on 12/05/14.
 * Contient les actions basiques pouvant etre utilisees dans les strategies.
 * ce package contient ausssi les capacités pouvant être utilisées par les personnages
 */
public class Actions implements IActions {

	/**
	 * Vue de l'element (pour l'interface graphique).
	 */
    private final VueElement ve;
    /**
     * Ref RMI et les vues des voisins.
     */
    private Hashtable<Integer,VueElement> voisins;
    /**
     * Initialise a faux, vrai si une action a deja ete executee.
     */
    private boolean actionExecutee;
    
    private boolean result;

    public Actions(VueElement ve, Hashtable<Integer, VueElement> voisins) {
        this.ve = ve;
        
        result = false;
        
        if (voisins == null) {
        	this.setVoisins(new Hashtable<Integer,VueElement>());
        } else {
        	this.setVoisins(voisins);
        }
        
        actionExecutee = false;
    }
	
	/**
	 * Appele par l'element. Permet a l'element ref1 (Personnage) de ramasser l'element ref2 (Potion), 
	 * qui modifie les caracteristiques du personnage.
	 * @param ref1 personnage ramassant la potion
	 * @param ref2 potion ramassee
	 * @param arene arene
	 */
	public void ramasser(int ref1, int ref2, IArene arene) throws RemoteException {
    	if(actionExecutee) {
    		System.err.println("Une action a deja ete executee pendant ce tour !");
    	} else {
			//recupere le combattant et la potion			
		    IConsole combattant = arene.consoleFromRef(ref1); 
		    IConsole potion = arene.consoleFromRef(ref2); 

		    // effectue le ramassage
		    // si les deux sont vivants (ils peuvent etre presents mais sans vie)
		    if(combattant.getElement().getVie() > 0 && potion.getElement().getVie() > 0) { 
			    VueElement vCombattant = combattant.getVueElement();
			    Actions ramassage = new Actions(vCombattant, null);
			    
			    ramassage.ramasserPotion(potion, combattant);
		    	 
				actionExecutee = true;
		    }
    	}
	}
	
	
	/**
	 * Ramasse et utilise une potion sur un personnage specifiee. Ne doit pas 
	 * etre appele tel quel, mais est utilise dans {@code ramasser}.
	 * @param pot potion
	 * @param per personnage
	 * @throws RemoteException
	 */
	private void ramasserPotion(IConsole pot, IConsole per) throws RemoteException {
		Hashtable<String, Integer> nouvellesValeursPer = new Hashtable<String, Integer>();
		Hashtable<String, Integer> valeursPot = pot.getElement().getCaract();
		
		Enumeration<String> enumCaract = valeursPot.keys();
		
		while (enumCaract.hasMoreElements()) {
			String s = enumCaract.nextElement();
			Integer val = per.getElement().getCaract(s);
			
			if (val != null) {
				nouvellesValeursPer.put(s, val + valeursPot.get(s));
			}
			
			//valeursPot.put(s, 0); //on vide toute la potion, meme si elle ne correspond pas aux caract. du perso ?
			
			//pot.majCaractElement(valeursPot);
		}
		
		// mise a jour du personnage
		per.majCaractElement(nouvellesValeursPer);
		
    	//mets a jour l'etat de la potion comme ramassee (plus de vie)
    	pot.perdreVie(1);
	}

	/**
	 * Appele par le run de la console. Permet a l'attaquant (ref1) d'attaquer (executer une frappe) le defenseur (ref2).
	 * Les regles de combat s'appliquent (en fonction de la force, de la defense et de l'esquive).
	 * Les caracteristiques du defenseur sont mises a jour (pas d'impact sur attaquant)
	 * Les deux protagonistes ajoutent leur adversaire dans les elements deja vus. 
	 * @param ref1 attaquant
	 * @param ref2 defenseur
	 * @param arene arene
	 */
	public void interaction(int ref1, int ref2, IArene arene) throws RemoteException {
    	if(actionExecutee) {
    		System.err.println("Une action a deja ete executee pendant ce tour !");
    	} else {
			 // recupere l'attaquant et le defenseur
		    IConsole attaquant = arene.consoleFromRef(ref1);
		    IConsole defenseur = arene.consoleFromRef(ref2);
		     
		    // cree le duel
		    // si les deux sont vivants (ils peuvent etre presents mais sans vie)
		    if(attaquant.getElement().getVie() > 0 && defenseur.getElement().getVie() > 0) { 
			    DuelBasic duel = new DuelBasic(arene, attaquant, defenseur);
				
				duel.realiserCombat(); 
				
				result = duel.getResult();
				
				actionExecutee = true;
		    }
    	}
	}
	
	/**
	 * Courage est une capacite qui permet de donnner de la force à toute son équipe
	 * @param ve
	 * @param actions
	 * @throws RemoteException
	 */
	public void courage(VueElement ve, Actions actions, int capacite_bonus,ArrayList<Integer> list) throws RemoteException
	{	
		System.out.println(ve.getRef()+" :Ourra on a peut etre gagner une bataille mais pas la guerre");
		Hashtable<String, Integer> caract = new Hashtable<String, Integer>();
		
		IConsole cons;
	
		for(Integer pers : list){
			
			cons = ve.getControleur().getArene().consoleFromRef(pers);
			caract .put("force", cons.getElement().getCaract("force")+20);
			cons.majCaractElement(caract);
			
		}
			
	}
	
	/**
	 * Action permettant à l'assassin de réaliser ce pourquoi il est fait
	 * @param ref_assassin
	 * @param ref_victime
	 * @param arene
	 * @throws RemoteException
	 */
	public void assassinat(int ref_assassin, int ref_victime, IArene arene) throws RemoteException{
		IConsole assassin = arene.consoleFromRef(ref_assassin);
	    IConsole victime = arene.consoleFromRef(ref_victime);
	    
	    System.out.println(ve.getRef()+" :J'ai encore fait un bon boulot");
	    
		DuelBasic duel = new DuelBasic(arene,assassin ,victime );
		
		duel.assassinat(assassin, victime);
	}
	
	
	/**
	 * Fonction qui permet a un element de creer lui même une potion
	 * @throws RemoteException
	 */
	public void creerPotion() throws RemoteException{
		
		System.out.println(ve.getRef()+" :ouh ouh ouh en voila une potion !");
		
		IArene arene = ve.getControleur().getArene();
		try {
			Element anduril = new SuperPotion();
			Random r = new Random();
			new Console(anduril, r.nextInt(100),r.nextInt(100), arene.getPort(),arene.getIpname());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public Hashtable<Integer,VueElement> getVoisins() {
		return voisins;
	}



	public void setVoisins(Hashtable<Integer,VueElement> voisins) {
		this.voisins = voisins;
	}



	public VueElement getVe() {
		return ve;
	}
	
	public boolean getResult(){
		return(result);
	}
	
	
}
