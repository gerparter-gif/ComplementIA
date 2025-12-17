
public class MMMain {
	public static void main(String [] args) {
		Strategie strat = new StExpectimax();
		Jeu jeu = new Jeu(4, strat) ;
		jeu.lancer();
	}
}
