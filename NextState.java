/**
 * This is like a look forward state of the board
 * based on the current state
 * 
 * The first part look exactly like State (I copy and paste)
 * The difference is that in makeMove, turn is not increased.
 * There is also a constructor.
 * 
 * The second part is possible Heuristic features.
 * (This can be put in PlayerSkeleton but I think it makes more sense
 * to put here, as the features belong to a state of the board and the piece)
 *
 */
public class NextState {
	public static final int COLS = 10;
	public static final int ROWS = 21;
	public static final int N_PIECES = 7;

	public boolean lost = false;

	private int turn = 0;
	private int cleared = 0;

	//each square in the grid - int means empty - other values mean the turn it was placed
	private int[][] field = new int[ROWS][COLS];
	//top row+1 of each column
	//0 means empty
	private int[] top = new int[COLS];

	protected int nextPiece;

	//all legal moves - first index is piece type - then a list of 2-length arrays
	protected static int[][][] legalMoves = new int[N_PIECES][][];

	//indices for legalMoves
	public static final int ORIENT = 0;
	public static final int SLOT = 1;

	//possible orientations for a given piece type
	protected static int[] pOrients = {1,2,4,4,4,2,2};

	//the next several arrays define the piece vocabulary in detail
	//width of the pieces [piece ID][orientation]
	protected static int[][] pWidth = {
			{2},
			{1,4},
			{2,3,2,3},
			{2,3,2,3},
			{2,3,2,3},
			{3,2},
			{3,2}
	};
	//height of the pieces [piece ID][orientation]
	private static int[][] pHeight = {
			{2},
			{4,1},
			{3,2,3,2},
			{3,2,3,2},
			{3,2,3,2},
			{2,3},
			{2,3}
	};
	private static int[][][] pBottom = {
			{{0,0}},
			{{0},{0,0,0,0}},
			{{0,0},{0,1,1},{2,0},{0,0,0}},
			{{0,0},{0,0,0},{0,2},{1,1,0}},
			{{0,1},{1,0,1},{1,0},{0,0,0}},
			{{0,0,1},{1,0}},
			{{1,0,0},{0,1}}
	};
	private static int[][][] pTop = {
			{{2,2}},
			{{4},{1,1,1,1}},
			{{3,1},{2,2,2},{3,3},{1,1,2}},
			{{1,3},{2,1,1},{3,3},{2,2,2}},
			{{3,2},{2,2,2},{2,3},{1,2,1}},
			{{1,2,2},{3,2}},
			{{2,2,1},{2,3}}
	};
	
	public int[][] getField() {
		return field;
	}

	public int[] getTop() {
		return top;
	}

    public static int[] getpOrients() {
        return pOrients;
    }
    
    public static int[][] getpWidth() {
        return pWidth;
    }

    public static int[][] getpHeight() {
        return pHeight;
    }

    public static int[][][] getpBottom() {
        return pBottom;
    }

    public static int[][][] getpTop() {
        return pTop;
    }


	public int getNextPiece() {
		return nextPiece;
	}
	
	public boolean hasLost() {
		return lost;
	}
	
	public int getRowsCleared() {
		return cleared;
	}
	
	public int getTurnNumber() {
		return turn;
	}

	//returns false if you lose - true otherwise
	public boolean makeMove(int orient, int slot) {
		//no turn++ here
		//height if the first column makes contact
		int height = top[slot]-pBottom[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}

		//check if game ended
		if(height+pHeight[nextPiece][orient] >= ROWS) {
			lost = true;
			return false;
		}


		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {

			//from bottom to top of brick
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = turn;
			}
		}

		//adjust top
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}

		int rowsCleared = 0;

		//check for full rows - starting at the top
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for(int c = 0; c < COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
				cleared++;
				//for each column
				for(int c = 0; c < COLS; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}
			}
		}
		return true;
	}
	
	/** Constructor
	 * We need exactly these to make a new board. 
	 * (the piece is not needed to make a board before we even make a move)
	 * 
	 * @param turn
	 * @param field
	 * @param nextPiece
	 * @param top
	 */
	public NextState(int turn, int field[][], int nextPiece, int top[]) {
		this.turn = turn;
		for (int i = 0 ; i< ROWS; i++){
    		for (int j = 0; j< COLS; j++){
    			this.field[i][j] = field[i][j];
    		}
    	}
        this.nextPiece = nextPiece;
        this.top = top.clone();      
    }
	
/*************************************************************************************
 The following section is to design the Heuristic features
 **************************************************************************************/
	
	/**
	 * This heuristic calculates the absolute height difference between 
	 * column
	 * 
	 * @return the absolute height difference
	 */
	public int calculateMinHeightHeuristic() {
		int result = 0;
		for (int i = 0; i < COLS-1; i++ )
			result += Math.abs(top[i] - top[i+1]);
		return result;
	}
}