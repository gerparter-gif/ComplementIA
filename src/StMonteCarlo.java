import java.util.Random;

public class StMonteCarlo implements Strategie{
	// Nombre de simulations par coup
	// Plus c'est grand, plus c'est précis mais plus c'est lent
	private int nbSimulations;
	// Profondeur des simulations (nombre de coups simulés)
	// Si -1, on simule jusqu'à la fin de la partie
	private int profondeurSimulation;
	private Random random;
	// Constructeur avec paramètres
	public StMonteCarlo(int nbSimulations, int profondeurSimulation) {
		this.nbSimulations = nbSimulations;
		this.profondeurSimulation = profondeurSimulation;
		this.random = new Random();
	}
	// Constructeur par défaut : 100 simulations, profondeur 20
	public StMonteCarlo() {
		this(100, 10);
	}

	@Override
	public Direction choisirCoup(Tableau t) {
		Direction[] directions = {Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE};
		Direction meilleureCoup = directions[0];
		double meilleurScoreMoyen = Double.NEGATIVE_INFINITY;
		// Pour chaque direction possible
		for (int i = 0; i < directions.length; i++) {
			Direction dir = directions[i];
			// On teste si le coup est possible
			Tableau copie = new Tableau(t);
			boolean possible = jouerCoup(copie, dir);
			if (possible) {
				// On fait N simulations à partir de ce coup
				double scoreMoyen = simulerPlusieursParties(copie, nbSimulations);
				// Si c'est mieux que ce qu'on a trouvé
				if (scoreMoyen > meilleurScoreMoyen) {
					meilleurScoreMoyen = scoreMoyen;
					meilleureCoup = dir;
				}
			}
		}
		return meilleureCoup;
	}

	// Simule N parties aléatoires et retourne le score moyen
	private double simulerPlusieursParties(Tableau t, int nbSimulations) {
		double sommeScores = 0;
		// On fait N simulations
		for (int i = 0; i < nbSimulations; i++) {
			// Chaque simulation part d'une copie du plateau actuel
			Tableau copie = new Tableau(t);
			// On joue aléatoirement jusqu'à la fin (ou profondeur max)
			int scoreFinal = simulerUnePartie(copie);
			sommeScores += scoreFinal;
		}
		// On retourne la moyenne
		return sommeScores / nbSimulations;
	}



	// Simule UNE partie complète en jouant aléatoirement

	// Retourne le score final obtenu

	private int simulerUnePartie(Tableau t) {
		Direction[] directions = {Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE};
		int coupsJoues = 0;
		// On joue jusqu'à perdre ou atteindre la profondeur max
		while (!t.hasLost()) {
			// Si on a une limite de profondeur et qu'on l'atteint
			if (profondeurSimulation != -1 && coupsJoues >= profondeurSimulation) {
				break;
			}
			// On choisit une direction aléatoire
			Direction dir = directions[random.nextInt(4)];
			// On tente de jouer ce coup
			boolean reussi = jouerCoup(t, dir);
			// Si le coup a réussi, on compte
			if (reussi) {
				coupsJoues++;
			}
			// Sécurité : si aucun coup n'est possible, on arrête
			// (normalement hasLost() devrait le détecter)
			if (!existeCoupPossible(t)) {
				break;
			}
		}
		// On retourne le score final de cette simulation
		return t.getScore();
	}

	// Vérifie s'il existe au moins un coup possible

	private boolean existeCoupPossible(Tableau t) {
		Direction[] directions = {Direction.HAUT, Direction.BAS, Direction.GAUCHE, Direction.DROITE};
		for (int i = 0; i < directions.length; i++) {
			Tableau copie = new Tableau(t);
			if (jouerCoup(copie, directions[i])) {
				return true;
			}
		}
		return false;
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
