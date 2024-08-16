import java.util.Scanner;

public class H1 {
	
	static Scanner sc = new Scanner(System.in);
	private static final int n = 4;			// Size of game board (n*n matrix)
	private static final int k1 = 2;		// Values of two possible numbers we draw (k1,k2)
	private static final int k2 = 4;
	private static final double p1 = 0.7;	// & Their possibilities (p1,p2)
	private static final double p2 = 0.3;
	private static final int wNum = 2048;	// Winning tile
	private static double emptyCells = n*n;
	private static int[][] board;
	private static int[][] lastBoard;
	private static int best = 0;
	private static int score;
	private static int lastBest;
	private static int lastScore;
	private static boolean firstGame = true;
	private static boolean endGame;			// End game request
	private static boolean wantGame = true;	// End program request
	private static boolean won;
	private static boolean returned; // this variable tells us if we pressed 'r' last time
	
	public static void main(String[] args) {
		while(wantGame) {	// player still want to play
			initGame();
			while (!gameOver()) {
				printState();
				playerMove();
			}
		}
		System.out.println("Goodbye");	// player requested to end the program
	}
	
	public static void initGame() {
		if (firstGame) {
			firstGame(); // welcoming the player before starting, followed by startGame()
			firstGame = false;	// so we aren't triggering the welcome message ever again
		}
		else if(wantGame) // player chose 'n' last game, or lost and chose to start a new game
			endGame = false;
			startGame();
	}
	
	public static void firstGame() {
		System.out.println("Welcome, would you like to start the game?");
		char input = sc.next().charAt(0); // input from user
		if (input == 'y')
			startGame();
	}
	
	public static void startGame() {
		board = initBoard();
		lastBoard = initBoard();
		score = 0;
		won = false;
		returned = false;
		
		drawCell();			// drawing two tiles in the beginning of the game
		drawCell();
		saveState();
	}
	
	public static int[][] initBoard() {

		int[][] arr = new int[n][n];
		for(int i = 0; i < n; i++)
			for(int j = 0; j<n; j++)
				arr[i][j] = 0;
		return arr;
	}
	
	public static void printState() {
		printMatrix(board);
		System.out.println("Score: " + score + "\t" + "Best: " + best);
		System.out.println("w = up, s = down, a = left, d = right, r = return, e = exit, n = new game");
	}
	
	public static void printMatrix(int[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++)
				System.out.print(arr[i][j] + "\t");
			System.out.println();
		}
	}
	
	/* we draw a number between 0 and 'emptyCells' value minus one (<16)
	   then proceed to draw a num at the respective free cell
	   i.e if we drew 8, we draw a num at the 8th empty cell we found */
	public static void drawCell() {
		int cellIndex = (int)(Math.random()*emptyCells);
		int count = 0; // counts the index of the empty cell.
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
			{
				if(board[i][j] == 0) {
					if(count == cellIndex) {
						drawNum(i,j); // draw cell if we arrived at its index.
						return;
					}
					count++;
				}
			}
	}
	
	public static void drawNum(int i, int j) {
		double p = Math.random();
		if (p <= p1)
			board[i][j] = k1;
		else
			board[i][j] = k2;
		emptyCells--;
	}
	
	public static void playerMove() {
		char input = sc.next().charAt(0);
		
		while (!validMove(input)) {
			System.out.println("Invalid move, please pick a different move");
			input = sc.next().charAt(0);
		}
		
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
			returned = false;
			drawCell(); // 3
		}
	}
	
	public static void executeMove(char input) {
		saveState();
		switch(input) {
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
	public static void moveLeft() {
		mergeRows();
		for(int i = 0; i < n; i++)
			for(int j = 1; j < n; j++)
				if(board[i][j] != 0)
					moveCellLeft(i,j);
	}
	
	// merges before moving
	public static void mergeRows() {
		for(int i = 0; i < n; i++)
			mergeRow(i);
	}
	
	// merges row i
	public static void mergeRow(int i) {
		// cell 1
		for(int col = 0; col < n-1; col++) {
			if(board[i][col] == 0)
				continue;
			if(board[i][col] != 0) // a value, lets see if we can merge him with the next num
				// cell 2
				for(int j = col + 1; j < n; j++)
					if(board[i][j] != 0)
					{
						// merges cells only if they match values
						if(board[i][col] == board[i][j])
							mergeCells(i, col, j);
						col = j;
					}
		}
	}
	
	// merges cells in row, col1 and col2.
	public static void mergeCells(int row, int col1, int col2) {
		score += board[row][col1];
		if(score > best)
			best = score;
		board[row][col1] *= 2;
		board[row][col2] = 0;
		emptyCells++;
	}
	
	// move cell towards the left
	public static void moveCellLeft(int row, int col) {
		int k = col;
		for(int j = col - 1; j >= 0; j--, k--) {
			// keep moving the cell 1 slot to the left, if it is empty
			if(board[row][j] == 0) {
					board[row][j] = board[row][k];
					board[row][k] = 0;
			}
			else // stop moving the cell if we reached another cell
				break;
		}
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
	
	public static void saveState() {
		lastScore = score;
		lastBest = best;
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				lastBoard[i][j] = board[i][j];
	}
	
	public static void revertState() {
		score = lastScore;
		best = lastBest;
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				board[i][j] = lastBoard[i][j];
	}
	
	
	public static boolean gameOver() {
		if (returned)
			return false;
		if (won) {
			System.out.println("You won 2048! Would you like to continue? Press y if yes, n if not");
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
			System.out.println("Game over your score is " + score + ". For a new game press y, n if not");
			char input = sc.next().charAt(0);
			if (input == 'n')
				wantGame = false;
			return true;
		}
		
		if(wantGame == false)
			return true;
		return false;
	}
	
	public static boolean noMoves() {
		if (!validMove('w') && !validMove('a') && !validMove('s') && !validMove('d')) {
			return true;
		}
		return false;
	}
	
	public static boolean validMove(char move) {
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
	
	public static boolean validLeft() {
		for(int i = 0; i < n; i++)
			for(int j = 1; j < n; j++) // go through all cells except left wall
				// if current cell isn't a zero, and left to him there's a zero or equal cell
				if(board[i][j] != 0)
					if (board[i][j-1] == 0 || board[i][j-1] == board[i][j])
						return true; // moving left is valid
		return false;
	}
	
	public static boolean validDown() {
		rotateRight(board);
		boolean valid = validLeft();
		rotateRight(board);
		rotateRight(board);
		rotateRight(board);
		return valid;
	}
	
	public static boolean validRight() {
		rotateRight(board);
		rotateRight(board);
		boolean valid = validLeft();
		rotateRight(board);
		rotateRight(board);
		return valid;
	}
	
	public static boolean validUp() {
		rotateRight(board);
		rotateRight(board);
		rotateRight(board);
		boolean valid = validLeft();
		rotateRight(board);
		return valid;
	}
	
	
	public static void rotateRight(int[][] board) {
		int[][] rotated = new int[n][n]; // temp array we use to rotate every cell
		int j = n;
		for (int row = 0; row < n; row++, j--)
			for (int col = 0; col < n; col++)
				rotated[col][j-1] = board[row][col];
		
		for (int row = 0; row < n; row++) // 
			for (int col = 0; col < n; col++)
				board[row][col] = rotated[row][col];
	}

//					main input to test rotate
//	int k = 1;
//	int[][] arr = new int[4][4];
//	for (int i = 0; i < 4; i++)
//		for (int j = 0; j < 4; j++) {
//			arr[i][j] = k;
//			k++;
//		}
//	rotateRight(arr);
//	for (int i = 0; i < 4; i++) {
//		for (int j = 0; j < 4; j++) {
//			System.out.print(arr[i][j] + "\t");
//		}
//		System.out.println();
//	}
	
}
