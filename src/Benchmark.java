import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Benchmark {
	// Classe interne : stocke le resultat d'une partie
	// Classe interne : stocke le resultat d'une partie
	static class ResultatPartie {
		String strategie; // Nom de la strategie testee
		int numero; // Numero de la partie (1 a 100)
		int score; // Score final obtenu
		int coups; // Nombre de coups joues
		int tuileMax; // Plus grande tuile atteinte (2048, 1024, etc.)
		long temps; // Temps d'execution en millisecondes

		public ResultatPartie(String strategie, int numero, int score, 
				int coups, int tuileMax, long temps) {
			this.strategie = strategie;
			this.numero = numero;
			this.score = score;
			this.coups = coups;
			this.tuileMax = tuileMax;
			this.temps = temps;

		}

		// Convertit le resultat en une ligne CSV
		public String toCSV() {
			return strategie.replace(",", "_") + "," +
					numero + "," +
					score + "," +
					coups + "," +
					tuileMax + "," +
					temps;
		}

	}

	// Classe interne : calcule et affiche les statistiques d'une strategie
	static class Stats {
		String nom; // Nom de la strategie
		ArrayList<Integer> scores; // Liste de tous les scores obtenus
		ArrayList<Integer> coups; // Liste de tous les nombres de coups
		ArrayList<Integer> tuiles; // Liste de toutes les tuiles max atteintes
		ArrayList<Long> temps; // Liste de tous les temps d'execution

		public Stats(String nom) {
			this.nom = nom;
			this.scores = new ArrayList<>();
			this.coups = new ArrayList<>();
			this.tuiles = new ArrayList<>();
			this.temps = new ArrayList<>();
		}

		// Ajoute les resultats d'une partie aux statistiques
		public void ajouter(int score, int coups, int tuile, long temps) {
			this.scores.add(score);
			this.coups.add(coups);
			this.tuiles.add(tuile);
			this.temps.add(temps);
		}

		// Affiche toutes les statistiques de la strategie
		public void afficher() {
			// On trie pour pouvoir calculer mediane, min, max, etc.
			Collections.sort(scores);
			Collections.sort(temps);
			System.out.println("\n" + nom);

			// Affichage des statistiques sur les scores
			System.out.println("\nScores :");
			System.out.println(" Moyenne : " + moyenne(scores));
			System.out.println(" Mediane : " + mediane(scores));
			System.out.println(" Ecart-type : " + String.format("%.2f", ecartType(scores)));
			System.out.println(" Min : " + scores.get(0));
			System.out.println(" Max : " + scores.get(scores.size() - 1));
			System.out.println(" Q1 : " + quartile(scores, 0.25));
			System.out.println(" Q3 : " + quartile(scores, 0.75));

			// Affichage de la repartition des tuiles max

			System.out.println("\nTuiles max :");

			afficherTuiles();


			// Affichage des statistiques sur le nombre de coups
			System.out.println("\nCoups :");
			System.out.println(" Moyenne : " + moyenne(coups));
			System.out.println(" Mediane : " + mediane(coups));

			// Affichage des statistiques de temps d'execution
			System.out.println("\nTemps :");
			System.out.println(" Total : " + somme(temps) + " ms");
			System.out.println(" Moyen : " + (long)moyenne(temps) + " ms");
			System.out.println(" Median : " + (long)medianeLong(temps) + " ms");
			System.out.println(" Par coup : " + 
					String.format("%.2f ms", moyenne(temps) / (double)moyenne(coups)));
		}


		// Affiche la repartition des tuiles max atteintes (combien de fois 2048, 1024, etc.)
		private void afficherTuiles() {

			// Tableau pour compter : index = exposant (2^i)
			// Ex: tuile 2048 = 2^11, donc compteurs[11]++

			int[] compteurs = new int[15];

			// On compte chaque tuile
			for (int i = 0; i < tuiles.size(); i++) {
				int t = tuiles.get(i);
				// On calcule l'exposant : 2048 = 2^11, log2(2048) = 11
				int exp = (int)(Math.log(t) / Math.log(2));
				if (exp >= 0 && exp < compteurs.length) {
					compteurs[exp]++;
				}
			}

			// On affiche du plus grand au plus petit (2048, 1024, 512, etc.)
			for (int i = compteurs.length - 1; i >= 1; i--) {
				if (compteurs[i] > 0) {
					// On recalcule la valeur de la tuile : 2^i
					int val = (int)Math.pow(2, i);
					System.out.println(" " + val + " : " + compteurs[i] + " fois (" + 
							String.format("%.1f%%", compteurs[i] * 100.0 / tuiles.size()) + ")");
				}
			}
		}

		// Calcule la moyenne d'une liste de nombres

		private double moyenne(ArrayList<? extends Number> liste) {
			double s = 0;
			for (int i = 0; i < liste.size(); i++) {
				s += liste.get(i).doubleValue();
			}
			return s / liste.size();
		}

		// Calcule la mediane d'une liste d'entiers (liste doit etre triee)
		private double mediane(ArrayList<Integer> liste) {
			int n = liste.size();
			if (n % 2 == 0) {
				return (liste.get(n/2 - 1) + liste.get(n/2)) / 2.0;
			} else {
				return liste.get(n/2);
			}
		}

		// Calcule la mediane d'une liste de long (liste doit etre triee)
		private double medianeLong(ArrayList<Long> liste) {
			int n = liste.size();
			if (n % 2 == 0) {
				return (liste.get(n/2 - 1) + liste.get(n/2)) / 2.0;
			} else {
				return liste.get(n/2);
			}
		}

		// Calcule un quartile (Q1 = 0.25, Q3 = 0.75)
		private double quartile(ArrayList<Integer> liste, double p) {
			int idx = (int)Math.ceil(p * liste.size()) - 1;
			return liste.get(Math.max(0, Math.min(idx, liste.size() - 1)));
		}

		// Calcule l'ecart-type (mesure de dispersion des valeurs)
		private double ecartType(ArrayList<Integer> liste) {
			double moy = moyenne(liste);
			double somme = 0;
			for (int i = 0; i < liste.size(); i++) {
				int val = liste.get(i);
				somme += Math.pow(val - moy, 2);
			}
			return Math.sqrt(somme / liste.size());
		}

		// Calcule la somme d'une liste de long

		private long somme(ArrayList<Long> liste) {
			long s = 0;
			for (int i = 0; i < liste.size(); i++) {
				s += liste.get(i);
			}
			return s;
		}
	}
	// Lance une partie complete avec une strategie donnee
	// Retourne les resultats de cette partie

	private static ResultatPartie lancerPartie(String nom, Strategie strategie, 
			int numero, int taille) {
		Tableau tableau = new Tableau(taille);
		int coups = 0;
		int coupsInvalides = 0;
		final int MAX_COUPS_INVALIDES = 100; // Securite anti-boucle infinie
		long debut = System.currentTimeMillis();

		// Boucle principale du jeu : on joue jusqu'a perdre
		while (!tableau.hasLost()) {

			// La strategie choisit un coup
			Direction dir = strategie.choisirCoup(new Tableau(tableau));

			// On tente de jouer ce coup
			boolean ok = jouerCoup(tableau, dir);

			if (ok) {
				// Coup valide : on compte et on reinitialise le compteur d'echecs
				coups++;
				coupsInvalides = 0; // Réinitialiser si coup valide
			} else {

				// Coup invalide : on compte l'echec
				coupsInvalides++;

				// Si trop de coups invalides consecutifs, on force l'arret
				if (coupsInvalides >= MAX_COUPS_INVALIDES) {
					System.err.println("ATTENTION: Partie " + nom + " #" + numero + 
							" bloquee (100 coups invalides). Arret force.");
					break;
				}
			}
		}

		// On calcule le temps d'execution
		long fin = System.currentTimeMillis();
		long temps = fin - debut;

		// On recupere le score final et la tuile max
		int score = tableau.getScore();
		int tuile = trouverTuileMax(tableau);

		// On retourne tous les resultats
		return new ResultatPartie(nom, numero, score, coups, tuile, temps);
	}


	// Joue un coup sur le tableau selon la direction donnee
	// Retourne true si le coup a marche, false sinon
	private static boolean jouerCoup(Tableau t, Direction d) {
		switch(d) {
		case GAUCHE: return t.gauche();
		case DROITE: return t.droite();
		case HAUT: return t.haut();
		case BAS: return t.bas();
		}
		return false;
	}


	// Trouve la plus grande tuile presente sur le plateau
	private static int trouverTuileMax(Tableau t) {

		Carre[][] grid = t.getGrid();

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

	// Teste une strategie sur N parties et enregistre les resultats

	private static void tester(String nom, Strategie strategie, 
			int nbParties, int taille,
			ArrayList<ResultatPartie> resultats,
			Stats stats) {
		System.out.println("\nLancement de " + nbParties + " parties : " + nom);

		// On lance toutes les parties
		for (int i = 0; i < nbParties; i++) {
			// Affichage de progression tous les 10 parties
			if ((i + 1) % 10 == 0) {
				System.out.print((i + 1) + "... ");
				System.out.flush();
			}

			// Lance une partie et enregistre le resultat
			ResultatPartie res = lancerPartie(nom, strategie, i + 1, taille);
			resultats.add(res);
			stats.ajouter(res.score, res.coups, res.tuileMax, res.temps);
		}
		System.out.println("\nTermine.");
	}

	// Methode principale : lance tous les tests

	public static void main(String[] args) {
		final int NB_PARTIES = 1000; // Nombre de parties par strategie
		final int TAILLE = 4; // Taille du plateau (4x4)

		// Listes pour stocker tous les resultats
		ArrayList<ResultatPartie> resultats = new ArrayList<>();
		ArrayList<Stats> stats = new ArrayList<>();

		System.out.println("Benchmark 2048 - " + NB_PARTIES + " parties par strategie\n");

		// Noms des strategies a tester
		String[] noms = {

				"Aleatoire",
				"Greedy", 
				"Vider",
				"Monotonicite",
				"MiniMax(3)",
				"Expectimax(4)",
				"MonteCarlo(1000,5)"
		};

		// Instances des strategies correspondantes
		Strategie[] strategies = {
				new StAleatoire(),
				new StGreedy(),
				new StVider(),
				new StMonotonicite(),
				new StMiniMax(3),
				new StExpectimax(4),
				new StMonteCarlo(1000, 5)
		};

		// On teste chaque strategie

		for (int i = 0; i < strategies.length; i++) {
			Stats s = new Stats(noms[i]);
			tester(noms[i], strategies[i], NB_PARTIES, TAILLE, resultats, s);
			stats.add(s);
		}

		// ─────────────────────────────────────────────────────────────
		// EXPORT CSV FINAL
		// ─────────────────────────────────────────────────────────────
		System.out.println("\nSauvegarde dans resultats_2048.csv...");

		String nomFichier = "resultats_2048.csv";

		try (PrintWriter w = new PrintWriter(new FileWriter(nomFichier, false))) {

			// En-tête CSV
			w.println("strategie,partie,score,coups,tuile_max,temps_ms");

			// On écrit chaque résultat proprement
			for (ResultatPartie r : resultats) {
				w.println(r.toCSV());
			}
			System.out.println("Fichier CSV généré : " + nomFichier);

		} catch (IOException e) {
			System.err.println("Erreur lors de l’écriture du CSV : " + e.getMessage());
		}

		// Affichage des statistiques detaillees pour chaque strategie

		for (int i = 0; i < stats.size(); i++) {
			Stats s = stats.get(i);
			s.afficher();
		}

		// Affichage du resume comparatif final
		System.out.println("\n\nResume comparatif\n");
		System.out.printf("%-20s %10s %10s %10s\n", "Strategie", "Score moy", "Mediane", "Max tuile");
		System.out.println("----------------------------------------------------------");

		// On affiche un resume pour chaque strategie

		for (int i = 0; i < stats.size(); i++) {
			Stats s = stats.get(i);
			Collections.sort(s.scores);
			Collections.sort(s.tuiles);
			System.out.printf("%-20s %10.0f %10.0f %10d\n", 
					s.nom, 
					s.scores.stream().mapToInt(Integer::intValue).average().orElse(0),
					s.mediane(s.scores),
					s.tuiles.get(s.tuiles.size() - 1));
		}
		System.out.println("\nAnalysez le CSV sur R pour plus de details.");
	}
}
