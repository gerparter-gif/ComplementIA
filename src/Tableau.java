import java.util.Random;

public class Tableau {
	private Carre[][] grid ;
	private int taille ;
	private int score ;
	private Random random ;
	
	public Tableau(int taille) {
		this.taille = taille ;
		this.grid = new Carre[taille][taille] ;
		this.random = new Random() ;
		initializeTableau() ;
	}
	// Constructeur par copie → utile pour IA Monte Carlo
	public Tableau(Tableau autre) {
		this.taille = autre.taille ;
		this.score = autre.score ;
		this.grid = new Carre[taille][taille] ;
		this.random = new Random() ;
		for(int i = 0 ; i < taille ; i++) {
			for(int j = 0 ; j < taille ; j++) {
				// clone profond
				this.grid[i][j] = autre.grid[i][j].clone() ;
			}
		}
	}

	// Initialise un plateau vide + deux tuiles
	private void initializeTableau() {
		for(int i = 0; i < taille; i++) {
			for(int j = 0; j < taille ; j++) {
				grid[i][j] = new Carre() ;
			}
		}
		addRandomCarre();
		addRandomCarre();
	}
	// Ajoute une tuile 2 (90%) ou 4 (10%)
	public void addRandomCarre() {
		int emptyCount = 0 ;
		for(int i = 0 ; i < taille; i++) {
			for(int j = 0 ; j < taille; j++) {
				if (grid[i][j].isEmpty()) emptyCount++;
			}
		}
		if (emptyCount == 0) return;
		int pos = random.nextInt(emptyCount);
		int k = 0;
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				if (grid[i][j].isEmpty()) {
					if (k == pos) {
						grid[i][j].setValue(random.nextDouble() < 0.9 ? 2 : 4);
						return;
					}
					k++;
				}
			}         
		}
	}
	// ---------------------------------------------------------

	//                MOUVEMENTS PRINCIPAUX

	// ---------------------------------------------------------
	public boolean gauche() {
		resetMergedFlags(); // indispensable
		boolean deplace = false; 
		for (int i = 0; i < taille; i++) {
			Carre[] ligne = grid[i];
			if (compress(ligne)) deplace = true;
			if (unir(ligne)) deplace = true;
			compress(ligne); // recompresser après fusions
		}
		if (deplace) addRandomCarre();
		return deplace;
	}
	public boolean droite() {
		resetMergedFlags();
		boolean deplace = false;
		for (int i = 0; i < taille; i++) {
			Carre[] ligne = reverse(grid[i]); // marche vers la droite
			if (compress(ligne)) deplace = true;
			if (unir(ligne)) deplace = true;
			compress(ligne);
			grid[i] = reverse(ligne);
		}
		if (deplace) addRandomCarre();
		return deplace;
	}
	
	public boolean haut() {
		resetMergedFlags();
		grid = transpose(grid);
		boolean moved = gauche();
		grid = transpose(grid);
		return moved;
	}
	public boolean bas() {
		resetMergedFlags();
		grid = transpose(grid);
		boolean moved = droite();
		grid = transpose(grid);
		return moved;
	}
	// Après chaque mouvement, on permet aux tuiles de refusionner au tour suivant
	private void resetMergedFlags() {
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				grid[i][j].setMerged(false);
			}
		}
	}
	// ---------------------------------------------------------

	//                 OUTILS : COMPRESS, UNIR, etc.

	// ---------------------------------------------------------
	private boolean compress(Carre[] ligne) {
		boolean change = false;
		int insertPosition = 0;
		Carre[] newLine = new Carre[taille];
		// créer une ligne vide
		for (int i = 0; i < taille; i++) newLine[i] = new Carre();
		for (int i = 0; i < taille; i++) {
			if (!ligne[i].isEmpty()) {
				newLine[insertPosition].setValue(ligne[i].getValue());
				insertPosition++;
			}
		}
		// appliquer newLine à ligne
		for (int i = 0; i < taille; i++) {
			if (ligne[i].getValue() != newLine[i].getValue()) change = true;
			ligne[i].setValue(newLine[i].getValue());
		}
		return change;
	}

	private boolean unir(Carre[] ligne) {
		boolean change = false; 
		for (int i = 0; i < taille - 1; i++) {
			if (!ligne[i].isEmpty() &&
					ligne[i].getValue() == ligne[i + 1].getValue() &&
					!ligne[i].isMerged()) {
				// fusion
				ligne[i].setValue(ligne[i].getValue() * 2);
				ligne[i].setMerged(true);
				score += ligne[i].getValue();
				ligne[i + 1].setValue(0);
				change = true;
			}
		}
		return change;
	}
	
	private Carre[] reverse(Carre[] ligne) {
		Carre[] reversed = new Carre[taille];
		for (int i = 0; i < taille; i++) {
			// ❗ crée des clones pour éviter partages de référence
			reversed[i] = new Carre(ligne[taille - 1 - i].getValue());
		}
		return reversed;
	}

	private Carre[][] transpose(Carre[][] g) {
		Carre[][] t = new Carre[taille][taille];
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				t[i][j] = new Carre(g[j][i].getValue());
			}
		}
		return t;
	}
	// Vérifie si plus aucun mouvement possible
	public boolean hasLost() {
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				if (grid[i][j].isEmpty()) return false;
				if (i < taille - 1 && grid[i][j].getValue() == grid[i + 1][j].getValue()) 
					return false;
				if (j < taille - 1 && grid[i][j].getValue() == grid[i][j + 1].getValue()) 
					return false;
			}
		}
		return true;
	}

	public int getScore() {
		return score;
	}

	public Carre[][] getGrid() {
		return grid;
	}
}
