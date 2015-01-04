package element;

import interaction.Special;



public class Roi extends Personnage implements Special {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Roi () {
		super("Roi", 10, 50);
	}
	
	public void capacite(){
		System.out.println("roi");
	}
}
