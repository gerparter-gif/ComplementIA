
public class StVider implements Strategie{
	public Direction choisirCoup(Tableau t) {

        Direction meilleurCoup = Direction.GAUCHE;
        int maxVides = -1;

        for (Direction d : Direction.values()) {
            // On clone le tableau pour simuler
        	Tableau copie = new Tableau(t);

            boolean mouvementPossible = false;
            switch (d) {
                case GAUCHE:
                    mouvementPossible = copie.gauche();
                    break;
                case DROITE:
                    mouvementPossible = copie.droite();
                    break;
                case HAUT:
                    mouvementPossible = copie.haut();
                    break;
                case BAS:
                    mouvementPossible = copie.bas();
                    break;
            }

            // Si le coup ne bouge rien, on l'ignore
            if (!mouvementPossible) continue;
            int nbVides = compterVides(copie);
            if (nbVides > maxVides) {
                maxVides = nbVides;
                meilleurCoup = d;
            }
        }
        return meilleurCoup;
    }

    private int compterVides(Tableau t) {
        Carre[][] g = t.getGrid();
        int count = 0;
        
        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                if (g[i][j].isEmpty()) count++;
            }
        }
        return count;
    }
}
