
public class StMiniMax implements Strategie {
	// Profondeur de recherche : combien de coups on regarde à l'avance
		// Plus c'est grand, plus c'est intelligent mais plus c'est lent

		private int profondeur;
		public StMiniMax(int profondeur) {
			this.profondeur = profondeur;
		}

		// Constructeur par défaut : profondeur 3
		public StMiniMax() {
			this(3);
		}

		public Direction choisirCoup(Tableau t) {
			Direction[] directions = {Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE};
			Direction meilleureCoup = directions[0];
			double meilleureValeur = Double.NEGATIVE_INFINITY;
			// Pour chaque direction possible
			for (int i = 0; i < directions.length; i++) {
				Direction dir = directions[i];
				
				// On copie le plateau pour tester
				Tableau copie = new Tableau(t);
				boolean possible = jouerCoup(copie, dir);

				// Si le coup est possible
				if (possible) {
					// On évalue ce coup avec minimax
					// C'est maintenant au tour du jeu d'ajouter une tuile (MIN)
					double valeur = minimiser(copie, profondeur - 1);
					// On garde le meilleur coup
					if (valeur > meilleureValeur) {
						meilleureValeur = valeur;
						meilleureCoup = dir;
					}
				}
			}
			return meilleureCoup;
		}

		// Phase MAXIMISER : on cherche le meilleur coup du joueur
		// Retourne la meilleure valeur qu'on peut atteindre
		private double maximiser(Tableau t, int profondeur) {
			// Condition d'arrêt 1 : profondeur max atteinte
			if (profondeur == 0) {
				return evaluerPlateau(t);
			}

			// Condition d'arrêt 2 : partie perdue
			if (t.hasLost()) {
				return evaluerPlateau(t);
			}
			
			Direction[] directions = {Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE};
			double maxValeur = Double.NEGATIVE_INFINITY;

			// On teste toutes les directions
			for (int i = 0; i < directions.length; i++) {
				Tableau copie = new Tableau(t);
				boolean possible = jouerCoup(copie, directions[i]);
				if (possible) {
					// Après notre coup, c'est au jeu d'ajouter une tuile (MIN)
					double valeur = minimiser(copie, profondeur - 1);
					maxValeur = Math.max(maxValeur, valeur);
				}
			}

			// Si aucun coup n'est possible, on évalue le plateau actuel
			if (maxValeur == Double.NEGATIVE_INFINITY) {
				return evaluerPlateau(t);
			}
			return maxValeur;
		}

		// Phase MINIMISER : le jeu ajoute une tuile (adversaire)
		// On suppose le pire cas : la tuile qui nous donne le moins de points
		private double minimiser(Tableau t, int profondeur) {
			// Condition d'arrêt
			if (profondeur == 0) {
				return evaluerPlateau(t);
			}

			// On récupère toutes les cases vides
			Carre[][] grid = t.getGrid();
			int taille = grid.length;
			double minValeur = Double.POSITIVE_INFINITY;
			int nbCasesVides = 0;

			// Pour chaque case vide, on teste d'ajouter un 2 ou un 4
			for (int i = 0; i < taille; i++) {
				for (int j = 0; j < taille; j++) {
					if (grid[i][j].isEmpty()) {
						nbCasesVides++;
						// On teste avec un 2
						Tableau copie2 = new Tableau(t);
						copie2.getGrid()[i][j].setValue(2);
						double valeur2 = maximiser(copie2, profondeur - 1);

						// On teste avec un 4
						Tableau copie4 = new Tableau(t);
						copie4.getGrid()[i][j].setValue(4);
						double valeur4 = maximiser(copie4, profondeur - 1);

						// On prend le minimum (pire cas pour nous)
						// Pondéré : 90% de chance d'avoir un 2, 10% un 4
						double valeurMoyenne = 0.9 * valeur2 + 0.1 * valeur4;
						minValeur = Math.min(minValeur, valeurMoyenne);
					}
				}
			}

			// S'il n'y a pas de cases vides, on retourne l'évaluation actuelle
			if (nbCasesVides == 0) {
				return evaluerPlateau(t);
			}
			return minValeur;
		}

		// Évalue la qualité d'un plateau
		// Plus le score est élevé, mieux c'est
		private double evaluerPlateau(Tableau t) {
			double score = 0;
			// Critère 1 : le score du jeu (très important)
			score += t.getScore() * 1.0;
			// Critère 2 : nombre de cases vides (important d'avoir de l'espace)
			Carre[][] grid = t.getGrid();
			int taille = grid.length;
			int casesVides = 0;
			for (int i = 0; i < taille; i++) {
				for (int j = 0; j < taille; j++) {
					if (grid[i][j].isEmpty()) {
						casesVides++;
					}
				}
			}
			score += casesVides * 100; // Bonus pour les cases vides
			
			// Critère 3 : bonus si les grandes valeurs sont dans les coins
			// (stratégie classique du 2048)
			int maxVal = trouverMax(grid);
			if (grid[0][0].getValue() == maxVal || 
					grid[0][taille-1].getValue() == maxVal ||
					grid[taille-1][0].getValue() == maxVal || 
					grid[taille-1][taille-1].getValue() == maxVal) {
				score += 500; // Gros bonus si max dans un coin
			}
			return score;
		}
		// Trouve la valeur maximale sur le plateau
		private int trouverMax(Carre[][] grid) {
			int max = 0;
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[i].length; j++) {
					if (grid[i][j].getValue() > max) {
						max = grid[i][j].getValue();
					}
				}
			}
			return max;
		}
		// Joue un coup sur le tableau
		private boolean jouerCoup(Tableau t, Direction d) {
			switch(d) {
			case GAUCHE: return t.gauche();
			case DROITE: return t.droite();
			case HAUT: return t.haut();
			case BAS: return t.bas();
			}
			return false;
		}
}
