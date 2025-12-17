
public class StExpectimax implements Strategie{
	// Profondeur de recherche : combien de coups on regarde à l'avance
	// Expectimax peut aller un peu plus loin que Minimax car il est plus rapide
	private int profondeur;
	public StExpectimax(int profondeur) {
		this.profondeur = profondeur;
	}

	// Constructeur par défaut : profondeur 4
	public StExpectimax() {
		this(4);
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
				// On évalue ce coup avec expectimax
				// C'est maintenant au tour du jeu d'ajouter une tuile (noeud CHANCE)
				double valeur = noeudChance(copie, profondeur - 1);
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
				// Après notre coup, c'est au jeu d'ajouter une tuile (CHANCE)
				double valeur = noeudChance(copie, profondeur - 1);
				maxValeur = Math.max(maxValeur, valeur);
			}
		}

		// Si aucun coup n'est possible, on évalue le plateau actuel
		if (maxValeur == Double.NEGATIVE_INFINITY) {
			return evaluerPlateau(t);
		}
		return maxValeur;
	}
	// NOEUD CHANCE : calcule l'espérance mathématique
	// Différence clé avec Minimax : on fait une MOYENNE au lieu de prendre le MIN
	// Cette moyenne est pondérée par les probabilités (90% pour 2, 10% pour 4)
	private double noeudChance(Tableau t, int profondeur) {
		// Condition d'arrêt
		if (profondeur == 0) {
			return evaluerPlateau(t);
		}
		// On récupère toutes les cases vides
		Carre[][] grid = t.getGrid();
		int taille = grid.length;
		double sommeValeurs = 0.0;
		int nbCasesVides = 0;
		// Pour chaque case vide
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				if (grid[i][j].isEmpty()) {
					nbCasesVides++;
					// Scénario 1 : on ajoute un 2 (probabilité 90%)
					Tableau copie2 = new Tableau(t);
					copie2.getGrid()[i][j].setValue(2);
					double valeur2 = maximiser(copie2, profondeur - 1);
					// Scénario 2 : on ajoute un 4 (probabilité 10%)
					Tableau copie4 = new Tableau(t);
					copie4.getGrid()[i][j].setValue(4);
					double valeur4 = maximiser(copie4, profondeur - 1);
					// Espérance pour cette case = probabilité * valeur
					// On suppose probabilité uniforme pour chaque case vide
					double esperanceCetteCase = 0.9 * valeur2 + 0.1 * valeur4;
					sommeValeurs += esperanceCetteCase;
				}
			}
		}

		// S'il n'y a pas de cases vides, on retourne l'évaluation actuelle
		if (nbCasesVides == 0) {
			return evaluerPlateau(t);
		}
		// On retourne la moyenne sur toutes les cases vides
		// Chaque case a une probabilité de 1/nbCasesVides d'être choisie
		return sommeValeurs / nbCasesVides;
	}

	// Évalue la qualité d'un plateau
	// Fonction d'évaluation améliorée pour Expectimax
	private double evaluerPlateau(Tableau t) {
		Carre[][] grid = t.getGrid();
		int taille = grid.length;
		double score = 0;
		// Critère 1 : le score du jeu (très important)
		score += t.getScore();

		// Critère 2 : nombre de cases vides (crucial pour la survie)
		int casesVides = 0;
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				if (grid[i][j].isEmpty()) {
					casesVides++;
				}
			}
		}

		score += casesVides * 150; // Bonus important pour l'espace libre
		// Critère 3 : monotonie (les valeurs doivent être ordonnées)
		// Une grille bien organisée a ses grandes valeurs d'un côté
		score += calculerMonotonie(grid) * 50;

		// Critère 4 : bonus si max dans un coin
		int maxVal = trouverMax(grid);
		if (grid[0][0].getValue() == maxVal || 
				grid[0][taille-1].getValue() == maxVal ||
				grid[taille-1][0].getValue() == maxVal || 
				grid[taille-1][taille-1].getValue() == maxVal) {
			score += 700; // Gros bonus pour max au coin
		}

		// Critère 5 : pénalité pour les grosses différences entre cases adjacentes
		score -= calculerRugosite(grid) * 10;
		return score;
	}



	// Calcule la monotonie du plateau (plus c'est ordonné, mieux c'est)
	// Une bonne grille a ses valeurs qui décroissent dans une direction
	private double calculerMonotonie(Carre[][] grid) {
		double monotonie = 0;
		int taille = grid.length;
		// Monotonie horizontale (lignes)
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille - 1; j++) {
				int val1 = grid[i][j].getValue();
				int val2 = grid[i][j + 1].getValue();
				if (val1 > 0 && val2 > 0) {
					if (val1 >= val2) monotonie += 1;
					if (val1 <= val2) monotonie += 1;
				}
			}
		}

		// Monotonie verticale (colonnes)
		for (int j = 0; j < taille; j++) {
			for (int i = 0; i < taille - 1; i++) {
				int val1 = grid[i][j].getValue();
				int val2 = grid[i + 1][j].getValue();
				if (val1 > 0 && val2 > 0) {
					if (val1 >= val2) monotonie += 1;
					if (val1 <= val2) monotonie += 1;
				}
			}
		}

		return monotonie;
	}

	// Calcule la "rugosité" du plateau (différences entre cases adjacentes)

	// Plus il y a de grosses différences, plus c'est difficile de fusionner

	private double calculerRugosite(Carre[][] grid) {
		double rugosite = 0;
		int taille = grid.length;

		// Horizontalement
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille - 1; j++) {
				rugosite += Math.abs(grid[i][j].getValue() - grid[i][j + 1].getValue());
			}
		}

		// Verticalement
		for (int j = 0; j < taille; j++) {
			for (int i = 0; i < taille - 1; i++) {
				rugosite += Math.abs(grid[i][j].getValue() - grid[i + 1][j].getValue());
			}
		}
		return rugosite;
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
