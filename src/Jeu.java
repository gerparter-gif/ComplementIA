import java.util.Scanner;
import java.lang.InterruptedException ;

public class Jeu {
	private Tableau tableau;
	private boolean automatique;
	private Strategie strategie;

	public Jeu(int taille) {
		this.tableau = new Tableau(taille) ;
		this.automatique = false ;
	}

	public Jeu(int taille, Strategie strategie) {
		this.tableau = new Tableau(taille) ;
		this.automatique = true ;
		this.strategie = strategie ;
	}

	// Utilisation du try-with-resources pour fermer automatiquement le Scanner
	public void lancer() {
	    try (Scanner sc = new Scanner(System.in)) {
	        while(true) {
	            afficher();// Mise à jour visuelle du plateau
	            
	         // Vérification de la condition de défaite
	            if(tableau.hasLost()) {
	                System.out.println("Partie terminée. Score : " + tableau.getScore());
	                break; // Sortie de la boucle
	            }
	            Direction dir;
	            if(automatique) {
	            	// Mode IA : on passe une copie du tableau à la stratégie pour 
	                // éviter qu'elle ne modifie le vrai plateau par erreur.
	                dir = strategie.choisirCoup(new Tableau(tableau)); 
	            } else {
	                System.out.print("Choisissez mouvement (z=haut, s=bas, q=gauche, d=droite) : ");
	                String c = sc.nextLine().trim();

	                switch (c) {
	                case "z": dir = Direction.HAUT; break;
	                case "s": dir = Direction.BAS; break;
	                case "q": dir = Direction.GAUCHE; break;
	                case "d": dir = Direction.DROITE; break;
	                default:
	                    System.out.println("Commande invalide.");
	                    continue; // Recommence la boucle sans jouer
	                }
	            }
	         // Exécution du mouvement
	            boolean ok = jouerCoup(dir);
	         // Si le coup n'a rien changé au plateau (ex: bloqué contre un mur)
	            if(!ok && !automatique) {
	                System.out.print("Impossible");
	            }
	         
	         // Temporisation en mode auto pour rendre le jeu "regardable" par un humain
	            if(automatique) {
	                try {
	                    Thread.sleep(50);
	                } catch(InterruptedException e) {}
	            }
	        }
	    }
	}
	
	private boolean jouerCoup(Direction d) {
		switch(d) {
		case GAUCHE : return tableau.gauche();
		case DROITE: return tableau.droite() ;
		case HAUT : return tableau.haut();
		case BAS : return tableau.bas() ;
		}
		return false ;
	}
	
	private void afficher() {
	ConsoleUI.displayBoard(tableau);
	}
}

