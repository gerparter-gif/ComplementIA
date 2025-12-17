
public class StMonotonicite implements Strategie{
	
	public Direction choisirCoup(Tableau t) {
		Direction meilleur = Direction.GAUCHE ;
		double meilleurScore = -1 ;
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
            double score = calculerMonotonicite(copie);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleur = d;
            }
		}
		return meilleur ; 
	}
	
    private int calculerMonotonicite(Tableau t) {

        int best = -1;

        for (int rotation = 0; rotation < 4; rotation++) {

            int current = 0;
            Carre[][] g = t.getGrid();
            int n = g.length;

            // Lignes : non-increasing (>=)
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n - 1; col++) {
                    if (g[row][col].getValue() >= g[row][col + 1].getValue()) {
                        current++;
                    }
                }
            }

            // Colonnes : non-increasing (>=)
            for (int col = 0; col < n; col++) {
                for (int row = 0; row < n - 1; row++) {
                    if (g[row][col].getValue() >= g[row + 1][col].getValue()) {
                        current++;
                    }
                }
            }

            if (current > best) best = current;

            // Rotation du tableau de 90°
            t = rotation90(t);
        }

        return best;
    }

    // -----------------------------------------------------------
    //            Rotation 90 degrés - conforme au papier
    // -----------------------------------------------------------
    private Tableau rotation90(Tableau src) {
        Carre[][] g = src.getGrid();
        int n = g.length;

        Tableau r = new Tableau(n);

        Carre[][] out = r.getGrid();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                out[j][n - 1 - i] = g[i][j].clone();
            }
        }
        return r;
    }
}

