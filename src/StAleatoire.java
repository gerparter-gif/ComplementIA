import java.util.Random;

public class StAleatoire implements Strategie{
	private Random r = new Random();
	
	public Direction choisirCoup(Tableau t) {
        int i = r.nextInt(4);
        switch (i) {
            case 0: return Direction.HAUT;
            case 1: return Direction.BAS;
            case 2: return Direction.GAUCHE;
            default: return Direction.DROITE;
        }
    }
}
