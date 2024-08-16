import java.util.Scanner;

public class Evolution2048 {

	private static Scanner sc = new Scanner(System.in);
	private static final int N = 4;
	private static final double P = 0.7; // probability of drawing 1 year old Monkey
	private static boolean wantGame = true, firstGame = true, endGame, won, returned;
	private static Tile[][] board, lastBoard;
	private static int score, best = 0, lastScore, lastBest, maxAge, emptyCells;

	public static void main(String[] args) {
		while (wantGame) {
			initGame();
			while (!gameOver()) {
				printState();
				playerMove();
			}
		}
		System.out.println("Goodbye");
	}

	public static void initGame() {
		if (firstGame) {
			System.out.println("Welcome, would you like to start the game?");
			char input = sc.next().charAt(0);
			if (input == 'y') {
				firstGame = false; // So we aren't triggering the welcome message ever again
				startGame();
			} else
				wantGame = false;
		} else
			startGame();
	}

	public static void startGame() {
		score = 0;
		won = false;
		returned = false;
		endGame = false;
		board = new Tile[N][N];
		lastBoard = new Tile[N][N];
		emptyCells = N * N;

		drawMaxAge();
		printBoard();
		System.out.println("\n");

		drawCell();
		drawCell();		
		saveState();
	}

	public static void drawMaxAge() {
		maxAge = (int) (Math.random() * 5 + 5);
		Tile.maxAge = maxAge;
		System.out.println("The max age is " + maxAge);
	}

	public static void printBoard() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++)
				if (board[i][j] != null)
					System.out.print(board[i][j].toString() + "\t");
				else
					System.out.print("#" + "\t");
			System.out.println();
		}
	}

	public static void printState() {
		printBoard();
		System.out.println("Score: " + score + "\t" + "Best: " + best + "\t" + "Money: " + totalMoney());
		System.out.println("w = up, s = down, a = left, d = right, r = return, e = exit, n = new game");
	}

	public static int totalMoney() {
		int sum = 0;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if (board[i][j] instanceof Human)
					sum += ((Human) board[i][j]).getMoney();
		return sum;
	}

	/*
	 * we draw a number between 0 and 'emptyCells' value minus one (<16) then
	 * proceed to draw a Monkey at the respective free cell i.e if we drew 8, we
	 * draw a Monkey at the 8th empty cell we found
	 */
	public static void drawCell() {
		int cellIndex = (int) (Math.random() * emptyCells);
		int count = 0; // counts the index of the empty cell.
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				if (board[i][j] == null) {
					if (count == cellIndex) {
						drawMonkey(i, j); // draw cell if we arrived at its index.
						return;
					}
					count++;
				}
			}
	}

	public static void drawMonkey(int i, int j) {
		if (Math.random() < P)
			board[i][j] = new Monkey(1);
		else
			board[i][j] = new Monkey(2);
		emptyCells--;
	}

	public static void playerMove() {
		char input = sc.next().charAt(0);

		while (!validMove(input)) {
			System.out.println("Invalid move, please pick a different move");
			input = sc.next().charAt(0);
		}
		returned = false;
		switch (input) {
		case 'e':
			wantGame = false;
			break;
		case 'n':
			endGame = true;
			break;
		case 'r':
			returned = true;
			revertState();
			break;
		default:
			executeMove(input);
			drawCell(); // 3
		}
	}

	public static void saveState() {
		lastScore = score;
		lastBest = best;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if(board[i][j] == null)
					lastBoard[i][j] = null;
				else
					lastBoard[i][j] = board[i][j].clone();
	}

	public static void revertState() {
		score = lastScore;
		best = lastBest;
		int countEmpty = 0;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if(lastBoard[i][j] == null) {
					board[i][j] = null;
					countEmpty++;
				}
				else
					board[i][j] = lastBoard[i][j].clone();
		emptyCells = countEmpty;
	}

	public static boolean gameOver() {
		if (returned)
			return false;
		if (checkWon()) {
			System.out.println("You won! Old Human! would you like to continue? Press y if yes, n if not");
			char input = sc.next().charAt(0);
			if (input == 'y')
				endGame = false; // 4
			if (input == 'n') {
				System.out.println("For a new game press y, n if not");
				char input2 = sc.next().charAt(0);
				if (input2 == 'y')
					return true;
				if (input2 == 'n') {
					wantGame = false;
					return true;
				}
			}
		}
		if (noMoves() || endGame) { // player either lost or requested to start a new game
			printBoard();
			System.out.println("Game over your score is " + score + ". For a new game press y, n if not");
			char input = sc.next().charAt(0);
			if (input == 'n')
				wantGame = false;
			return true;
		}

		if (wantGame == false)
			return true;
		return false;
	}

	// returns true if player wins the game
	private static boolean checkWon() {
		if(!won && checkMaxHuman()) {
			won = true; 		// this makes sure we can only win once.
			return true;
		}
		return false;
	}
	
	private static boolean checkMaxHuman() {
		for(int i = 0; i < N; i++)
			for(int j = 0; j < N; j++) {
				Tile t = board[i][j];
				if(t != null && t.winningTile()) // returns true if we have a winning Tile.
					return true;
			}
		return false;
	}
	
	private static boolean noMoves() {
		if (!validMove('w') && !validMove('a') && !validMove('s') && !validMove('d')) {
			return true;
		}
		return false;
	}

	private static boolean validMove(char move) {
		switch (move) {
		case 'a':
			return validLeft();
		case 's':
			return validDown();
		case 'd':
			return validRight();
		case 'w':
			return validUp();
		case 'e':
			return true;
		case 'n':
			return true;
		case 'r':
			return true;
		default:
			return false;
		}
	}

	private static boolean validLeft() {
		for (int i = 0; i < N; i++)
			for (int j = 1; j < N; j++) // go through all cells except left wall
				// if current cell isn't null, and left to him there's null or compatible cell
				if (board[i][j] != null)
					if (board[i][j - 1] == null || board[i][j - 1].compatible(board[i][j]))
						return true; // moving left is valid
		return false;
	}

	private static boolean validDown() {
		rotateRight(board);
		boolean valid = validLeft();
		rotateRight(board);
		rotateRight(board);
		rotateRight(board);
		return valid;
	}

	private static boolean validRight() {
		rotateRight(board);
		rotateRight(board);
		boolean valid = validLeft();
		rotateRight(board);
		rotateRight(board);
		return valid;
	}

	private static boolean validUp() {
		rotateRight(board);
		rotateRight(board);
		rotateRight(board);
		boolean valid = validLeft();
		rotateRight(board);
		return valid;
	}

	private static void rotateRight(Tile[][] board) {
		Tile[][] rotated = new Tile[N][N]; // temp array we use to rotate every cell
		int j = N;
		for (int row = 0; row < N; row++, j--)
			for (int col = 0; col < N; col++)
				if(board[row][col] == null)
					rotated[col][j - 1] = null;
				else
					rotated[col][j - 1] = board[row][col].clone();

		for (int row = 0; row < N; row++) //
			for (int col = 0; col < N; col++)
				if(rotated[row][col] == null)
					board[row][col] = null;
				else
					board[row][col] = rotated[row][col].clone();
	}

	private static void executeMove(char input) {
		saveState();
		switch (input) {
		case 'a':
			moveLeft();
			break;
		case 's':
			moveDown();
			break;
		case 'd':
			moveRight();
			break;
		case 'w':
			moveUp();
			break;
		}
	}

	// executes a left move on the board
	private static void moveLeft() {
		mergeRows();
		for (int i = 0; i < N; i++)
			for (int j = 1; j < N; j++)
				if (board[i][j] != null)
					moveTileLeft(i, j);
	}
	
	public static void moveDown() {
		rotateRight(board);
		moveLeft();
		rotateRight(board);
		rotateRight(board);
		rotateRight(board);
	}
	
	public static void moveRight() {
		rotateRight(board);
		rotateRight(board);
		moveLeft();
		rotateRight(board);
		rotateRight(board);
	}
	
	public static void moveUp() {
		rotateRight(board);
		rotateRight(board);
		rotateRight(board);
		moveLeft();
		rotateRight(board);
	}

	// move Tile towards the left
	public static void moveTileLeft(int row, int col) {
		int k = col;
		for(int j = col - 1; j >= 0; j--, k--) {
			// keep moving the Tile 1 slot to the left, if it is empty
			if(board[row][j] == null) {
					board[row][j] = board[row][k].clone();
					board[row][k] = null;
			}
			else // stop moving the Tile if we reached another Tile
				break;
		}
	}
	
	// merges before moving
	private static void mergeRows() {
		for (int i = 0; i < N; i++)
			mergeRow(i);
	}

	// merges row i
	private static void mergeRow(int i) {
		// Tile 1
		for (int col = 0; col < N - 1; col++) {
			if (board[i][col] != null) // we found a Tile, check if we can merge it with the next Tile
				// Tile 2
				for (int j = col + 1; j < N; j++)
					if(board[i][j] == null)
						continue;
					else {
						// merges Tiles only if they are compatible
						if (board[i][col] != null && board[i][col].compatible(board[i][j]))
							mergeTiles(i,col,j);
						col = j;
					}
		}
	}

	// merges the two Tiles, updates game-state accordingly
	private static void mergeTiles(int i, int col, int j) {
		if (board[i][col] instanceof Flintstone)
			incrementMonkeys();
		board[i][col] = board[i][col].merge(board[i][j]);
		board[i][j] = null; 	// delete the second Tile
		emptyCells++;
		// Update score and best
		score += board[i][col].getAge() * board[i][col].getFactor();
		checkBest();
	}
	
	private static void checkBest() {
		if (score > best)
			best = score;
	}

	// Increments all monkey ages by 1, if they are not already at the max age,
	// Assuming Tiles can only evolve when merged and meet the evolution requirements.
	private static void incrementMonkeys() {
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++) {
				Tile t = board[i][j];
				if (t != null && t instanceof Monkey && t.getAge() != maxAge) {
					t.setAge(t.getAge() + 1);
					score += t.getAge();	// update the score for each monkey we incremented
					checkBest();
				}
			}
	}
}