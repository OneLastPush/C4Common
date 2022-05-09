package game;

/**
 * Class Responsible for all game logic for connect4.
 * 
 * @author Julien Comtois, Frank Birikundavyi, Marjorie Olano Morales
 * @version 11/2/2015
 */
public class C4Game {

	// 3 wide buffer on each side for easily checking win conditions
	private int[][] board;
	// 1 is player, 2 is AI (didn't use 0 because that means empty)
	private int player;
	// Count of how many turns have been played
	private int turnCtr;
	// Keep track of last token played
	private int lastTokenRow;
	private int lastTokenCol;
	// Keep track if AI has won
	private boolean hasAIWon;

	public C4Game() {
		board = new int[12][13];
		player = 1;
		turnCtr = 0;
	}

	/**
	 * Check if the current player has played a winning move
	 * 
	 * @return 0 = no win, 1 = Player, 2 = AI
	 */
	public int checkWin() {
		if (lineFinder(lastTokenRow, lastTokenCol, 4, player)) {
			return player;
		}
		return 0;
	}

	/**
	 * Finds the lowest empty spot in a specific column.
	 * 
	 * @param col
	 *            Column to check
	 * @return The lowest empty spot in the column.
	 */
	public int findLowestRow(int col) {
		// Find lowest position available
		for (int currRow = 8; currRow >= 3; currRow--) {
			if (board[currRow][col] == 0) {
				return currRow;
			}
		}
		return -1;
	}

	/**
	 * Determines if the game ended in a tie.
	 * 
	 * @return True if game ended in a tie.
	 */
	public boolean findTie() {
		// 42 = board is full
		if (turnCtr == 42) {
			return true;
		}
		return false;
	}

	/**
	 * Get whether the AI has won.
	 * 
	 * @return True if the AI has won.
	 */
	public boolean getAIWinStatus() {
		return hasAIWon;
	}

	/**
	 * Plays the AI move.
	 * 
	 * @return 2 digit integer representing row/column of AI move
	 */
	public int makeAIMove() {
		player = turnCtr % 2 + 1;
		if (player != 2) {
			throw new IllegalArgumentException("Not the AI's turn");
		}
		int pos = determineMove();
		int row = pos / 10;
		int col = pos % 10;
		board[row][col] = player;
		lastTokenRow = row;
		lastTokenCol = col;
		turnCtr++;
		return pos;
	}

	/**
	 * Play the client's move if it's valid.
	 * 
	 * @param col
	 *            Column to play in.
	 * @return Row in which the move was played, or -1 if the column is full or
	 *         out of bounds.
	 */
	public int makeClientMove(int col) {
		player = turnCtr % 2 + 1;
		int row;
		// If it's AI turn or a player turn with a valid move
		if (validateMove(col)) {
			row = findLowestRow(col);
			// Place the marker on the board internally
			if (row == -1) {
				return -1;
			}
			board[row][col] = player;
			lastTokenRow = row;
			lastTokenCol = col;
			turnCtr++;
			return row;
		} else {
			return -1;
		}
	}

	/**
	 * Used by client to update client's version of the board
	 * 
	 * @param pos
	 *            2 digits integer representing row/column of the move
	 */
	public void placeAIMove(int pos) {
		board[pos / 10][pos % 10] = 2;
	}

	/**
	 * Ranks all possible moves and decides on the best one.
	 * 
	 * @return The position of the move to be made.
	 */
	private int determineMove() {
		// [Rank][Row/Col]
		int[] bestMove = new int[2];

		// Loop through each column (all 7 possible moves)
		for (int col = 3; col <= 9; col++) {
			int row = findLowestRow(col);
			if (row == -1) {
				continue;
			}
			int rank = 0;

			// Find a win for the AI
			if (lineFinder(row, col, 4, 2)) {

				return row * 10 + col;
			}
			// Block a player's imminent win
			if (lineFinder(row, col, 4, 1)) {
				rank = 3;
			}
			// Continue a line of 2
			else if (lineFinder(row, col, 3, 2)) {
				rank = 2;
			}
			// Block a player's line of 2
			else if (lineFinder(row, col, 3, 1)) {
				rank = 1;
			}
			// All other possibilities are low priority
			if (rank >= bestMove[0]) {
				bestMove[0] = rank;
				bestMove[1] = row * 10 + col;
			}
		}
		// Return position of best move
		return bestMove[1];
	}

	/**
	 * Main logic to detect wins and choose AI moves.
	 * 
	 * @param tokenRow
	 *            Token row index.
	 * @param tokenCol
	 *            Token column index.
	 * @param length
	 *            Length of line to find.
	 * @param player
	 *            1 is player, 2 is AI
	 * @return true if match found
	 */
	private boolean lineFinder(int tokenRow, int tokenCol, int length,
			int player) {
		boolean isFirstDirection = true;
		int countGoal = length - 1;
		// Check vertical
		for (int ctr = 1; ctr <= length; ctr++) {
			if (ctr == length) {
				return true;
			}
			// Check below token
			if (board[tokenRow + ctr][tokenCol] != player) {
				break;
			}
		}
		// Check horizontal
		for (int ctr = 1, lineCtr = 0; true; ctr++) {
			// Means we have found a match
			if (lineCtr == countGoal) {
				return true;
			}
			// We change to checking the other direction
			if (ctr == length) {
				if (isFirstDirection) {
					ctr = 1;
					isFirstDirection = false;
					// Will fall through to left check
				} else {
					break;
				}
			}
			if (isFirstDirection) {
				// Check to right of token
				if (board[tokenRow][tokenCol + ctr] == player) {
					lineCtr++;
				} else {
					ctr = 0;
					isFirstDirection = false;
				}
			} else {
				// Check to left of token
				if (board[tokenRow][tokenCol - ctr] == player) {
					lineCtr++;
				} else {
					break;
				}
			}
		}
		isFirstDirection = true;
		// Check diagonal
		for (int ctr = 1, lineCtr = 0; true; ctr++) {
			if (lineCtr == countGoal) {
				return true;
			}
			if (ctr == length) {
				if (isFirstDirection) {
					ctr = 1;
					isFirstDirection = false;
					// Will fall through to above-left check
				} else {
					break;
				}
			}
			if (isFirstDirection) {
				// Check below-right of token
				if (board[tokenRow + ctr][tokenCol + ctr] == player) {
					lineCtr++;
				} else {
					ctr = 0;
					isFirstDirection = false;
				}
			} else {
				// Check above-left of token
				if (board[tokenRow - ctr][tokenCol - ctr] == player) {
					lineCtr++;
				} else {
					break;
				}
			}
		}
		isFirstDirection = true;
		// Check other diagonal
		for (int ctr = 1, lineCtr = 0; true; ctr++) {
			if (lineCtr == countGoal) {
				return true;
			}
			if (ctr == length) {
				if (isFirstDirection) {
					ctr = 1;
					isFirstDirection = false;
					// Will fall through to below-left check
				} else {
					break;
				}
			}
			if (isFirstDirection) {
				// Check above-right of token
				if (board[tokenRow - ctr][tokenCol + ctr] == player) {
					lineCtr++;
				} else {
					ctr = 0;
					isFirstDirection = false;
				}
			} else {
				// Check below-left of token
				if (board[tokenRow + ctr][tokenCol - ctr] == player) {
					lineCtr++;
				} else {
					break;
				}
			}
		}
		// No match found
		return false;
	}

	/**
	 * Ensure the move is valid.
	 * 
	 * @param col
	 *            Column to check.
	 * @return True if valid, false otherwise.
	 */
	private boolean validateMove(int col) {
		// Check if the column is out of bounds or there is no space left in col
		if (col < 3 || col > 9 || board[3][col] != 0) {
			return false;
		}
		return true;
	}
}
