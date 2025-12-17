public class Carre {

	private int valeur ;
	private boolean uni ; // Indique si ce carre a déjà fusionné ce tour
	
	public Carre() {
		this(0); // Initialise une case vide par défaut
	}

	public Carre(int valeur) {
		this.valeur = valeur ;
		this.uni = false ; // Par défaut, une case n'est pas marquée comme fusionnée
	}

	public int getValue() {
		return valeur ;
	}

	public void setValue(int valeur) {
		this.valeur = valeur ;
	}

	public boolean isMerged() {
		return uni ;
	}

	public void setMerged(boolean uni) {
		this.uni = uni ;
	}

	public boolean isEmpty() {
		return valeur == 0 ;
	}
	
	public void unir(Carre autre) {
		// On ajoute la valeur de l'autre carré à celui-ci
		this.valeur += autre.getValue();
		// On marque ce carré comme "ayant déjà fusionné" pour ce tour-ci
		this.uni = true;
	}
	// Utilisé pour les copies profondes dans le constructeur par copie de Tableau
	public Carre clone() {
		return new Carre(this.valeur) ;
	}

}
