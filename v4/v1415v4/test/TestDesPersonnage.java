package test;
import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.*;


public class TestDesPersonnage {
	
	public static void main(String[] args) {

		String perso = new String();
		Personnage bidule = null;
		Random r;
		
		try {
			int port=5099;	//par defaut, port de l'arene=5099
			String ipArene = "localhost";
			
			if(args.length == 2){
				port = Integer.parseInt(args[0]);
				ipArene = args[1];
			}
			
			if(args.length == 3 ){
				perso = args[2];
			}
			
			if(args.length == 1 ){
				perso = args[0];
			}
			
			System.out.println(perso);
			
			if(perso.equals("roi")){
				bidule = new Roi();
				r = new Random();
				new Console(bidule, 50, 50, port, ipArene);
				return;
			}
			
			if(perso.equals("assassin")){
				bidule = new Assassin();
				r = new Random();
				new Console(bidule, 50, 50, port, ipArene);
				return;
			}
			
			if(perso.equals("mage")){
				bidule = new Mage();
				r = new Random();
				new Console(bidule, 50, 50, port, ipArene);
				return;
			}
	
			bidule = new Personnage("bidule",50,50);
			r = new Random();
			new Console(bidule, 50, 50, port, ipArene);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

}
