
public class ConsoleUI {
	// Couleurs ANSI

	private static final String RESET = "\u001B[0m";
	private static final String BOLD = "\u001B[1m";
	private static final String CYAN = "\u001B[36m";
	private static final String GREEN = "\u001B[32m";
	private static final String YELLOW = "\u001B[33m";
	private static final String MAGENTA = "\u001B[35m";
	private static final String RED = "\u001B[31m";

	// Assigne une couleur en fonction de la valeur

	private static String colorize(int value) {
		switch (value) {
		case 0: return RESET;
		case 2: case 4: return CYAN;
		case 8: case 16: return GREEN;
		case 32: case 64: return YELLOW;
		case 128: case 256: return RED;
		default: return MAGENTA; // 512, 1024, 2048, etc.
		}
	}

	// Retourne la représentation colorée d'une case

	private static String renderCell(Carre c) {
		int value = c.getValue();
		String valStr = value == 0 ? "." : String.valueOf(value);
		return colorize(value) + String.format("%4s", valStr) + RESET; // pour aligner les colonnes
	}

	// ---------------------------------------------------------

	// Affichage du plateau

	// ---------------------------------------------------------

	public static void displayBoard(Tableau t) {
		Carre[][] grid = t.getGrid();
		int size = grid.length;

		System.out.println(BOLD + "\n-----  2048  -----" + RESET);
		System.out.println("Score : " + t.getScore() + "\n");

		for (int i = 0; i < size; i++) {
			System.out.print("| ");
			for (int j = 0; j < size; j++) {
				System.out.print(renderCell(grid[i][j]) + " | ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
