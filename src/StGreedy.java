
public class StGreedy implements Strategie {
	public Direction choisirCoup(Tableau t) {

		// On teste les 4 directions
		Direction[] directions = {Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE};
		Direction meilleureCoup = directions[0];
		int meilleureScore = Integer.MIN_VALUE;

		// Pour chaque direction
		for (int i = 0; i < directions.length; i++) {
			Direction dir = directions[i];
			// On fait une copie du tableau pour tester sans modifier l'original
			Tableau copie = new Tableau(t);
			// On joue le coup sur la copie
			boolean possible = jouerCoup(copie, dir);
			// Si le coup est possible, on évalue son score
			if (possible) {
				int score = evaluerCoup(copie);
				// Si c'est mieux que ce qu'on a trouvé, on le garde
				if (score > meilleureScore) {
					meilleureScore = score;
					meilleureCoup = dir;
				}
			}
		}
		return meilleureCoup;
	}

	// Joue un coup sur le tableau et retourne si c'était possible
	private boolean jouerCoup(Tableau t, Direction d) {
		switch(d) {
		case GAUCHE: return t.gauche();
		case DROITE: return t.droite();
		case HAUT: return t.haut();
		case BAS: return t.bas();
		}
		return false;
	}

	// Évalue la qualité d'un coup (après qu'il soit joué)
	// Critère greedy : maximiser le score gagné

	private int evaluerCoup(Tableau t) {
		return t.getScore();
	}
}
