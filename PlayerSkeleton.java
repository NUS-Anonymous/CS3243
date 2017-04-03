
public class PlayerSkeleton {

	/**
	 * The heuristic features
	 * feature[0] - aggregate height
	 * feature[1] - rows cleared
	 * feature[2] - holes
	 * feature[3] - absolute height difference between columns (bumpiness)
	 * feature[4] - total bad gaps (that only the longest 4-length piece can fit it)
	 * feature[5] - is lost?
	 */
    
    public static final int NUM_OF_HEURISTICS = 6; 
	public int[] feature = new int[NUM_OF_HEURISTICS];
	
	//the linear weight for each feature, set others to 0 to test the correctness
	//numbers or rows cleared should have negative weights, cuz we want to award this
	public double[] weight = {0.510066, -0.76066, 0.35663, 0.184483,  0.13123, 10000};
	
	/**
	 * Set the weight vector for the player
	 * @param weight
	 */
    public void setWeightVector(double[] weight){
        for (int i = 0 ; i < NUM_OF_HEURISTICS; i++ ){
            this.weight[i] = weight[i];
        }
    }

    /**
     * Empty Constructor
     */
    public PlayerSkeleton() {}
    
	/**
	 * We generate all possible moves for the current piece
	 * Calculate the weighted heuristic value of the field after we make the move
	 * 
	 * Choose the move that has in the smallest heuristic value
	 * 
	 * @param s - current state
	 * @param legalMoves
	 * @return the best move based on the heuristic values
	 */
	public int pickMove(State s, int[][] legalMoves) {
		double min = Double.MAX_VALUE;
		int bestMove = -1;
		for (int i = 0; i < legalMoves.length; i++) {
			NextState tempState = new NextState(s.getTurnNumber(), s.getField(), 
														s.getNextPiece(), s.getTop());
			tempState.makeMove(legalMoves[i][NextState.ORIENT], legalMoves[i][NextState.SLOT]);
			double value = getWeightedHeuristic(tempState);
			//update value if find some smaller heuristic value
			if (value < min) {
				min = value;
				bestMove = i;
			}			
		}	
		return bestMove;
	}
	
	/**
	 * Get the weighted heuristic value of a NextState
	 * 
	 * @param s - next state
	 * @return the weighted heuristic of next state
	 */
	private double getWeightedHeuristic(NextState s) {
		double value = 0;
		
		feature[0] = s.getAggregateHeight();
		feature[1] = s.getRowsCleared();
		feature[2] = s.getHoles();
		feature[3] = s.getHeightDifference();
		feature[4] = s.getTotalBadGapSize();
		feature[5] = s.isLost();
	
	    for (int i = 0; i < NUM_OF_HEURISTICS; i++) {
	        value += weight[i] * feature[i]; 
		}
	    
	    return value;
	}

	/**
	 * Run the game
	 * @return number of rows cleared
	 */
    public int run(){
        State s = new State();
        
        while(!s.hasLost()) {
            s.makeMove(this.pickMove(s,s.legalMoves()));
        }
        
        return s.getRowsCleared();
    }	
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}	
}
