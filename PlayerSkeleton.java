
public class PlayerSkeleton {

    //switch to true to learn, false to run the game
    private static boolean isLearning = false;

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
    public double[] weight = {0.1598390901697,
            -13.04751328445677,
            86.34224744079854,
            13.742605537697884,
            20.13035165639796,
            999999.0};

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
        if(isLearning) {
//          GeneticAlgorithm GA = new GeneticAlgorithm();
//          GA.learn();
        } else {
            State s = new State();
            new TFrame(s);
            PlayerSkeleton p = new PlayerSkeleton();
            while(!s.hasLost()) {
                s.makeMove(p.pickMove(s,s.legalMoves()));
                s.draw();
                s.drawNext(0,0);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("You have completed "+s.getRowsCleared()+" rows.");
        }	
    }
}

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
class NextState {
    
/*******************************************************************************
 * 'Deep' copy of class State
 *******************************************************************************/
    
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
                    while(top[c]>=1 && field[top[c]-1][c]==0)   top[c]--;
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
     * This heuristic calculates the total aggregate height of all columns
     * This corresponds to feature[0]
     * 
     * @return the total aggregate height of all columns
     */
    public int getAggregateHeight() {
        int result = 0;
        for (int i = 0; i < COLS; i++) {
            result += top[i];
        }
        return result;
    }
    
    /**
     * This heuristic calculates the number of rows cleared
     * This corresponds to feature[1]
     * 
     * @return number of rows cleared
     */
    public int getRowsCleared() {
        return cleared;
    }
    
    /**
     * This heuristic calculates the number of holes in the board
     * This corresponds to feature[2]
     * 
     * @return number of holes
     */
    public int getHoles() {
        //Number of holes = total height - total grids used
        int result;
        int totalGrids = 0;
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if(field[i][j] > 0) {
                    totalGrids++;
                }                  
            }
        }
        result = getAggregateHeight() - totalGrids;
        return result;
        
    }
    
    /**
     * This heuristic calculates the absolute height difference between 
     * column
     * This corresponds to feature[3]
     * 
     * @return the absolute height difference
     */
    public int getHeightDifference() {
        int result = 0;
        for (int i = 0; i < COLS-1; i++ ) {
            result += Math.abs(top[i] - top[i+1]);
        }
        return result;
    }
    
    /**
     * A bad gap happens when middle column is shorter than adjacent columns 
     * by more than 2 gaps
     * 
     * @return total size of all the bad gaps
     */
    public int getTotalBadGapSize() {
        int result = 0;
        
        int leftDiff = 0;
        int rightDiff = 0;
        
        for (int i = 1; i < COLS - 1; i++) {
            leftDiff = top[i-1] - top[i];
            rightDiff = top[i+1] - top[i];
            
            if ((leftDiff >= 2) && (rightDiff >= 2)) {
                result += Math.min(leftDiff, rightDiff);
            }
        }
        
        //next to borders
        if (top[1] - top[0] >= 2) {
            result += top[1] - top[0];
        }
        
        if (top[COLS-2] - top[COLS-1] >= 2) {
            result += top[COLS-2] - top[COLS-1];
        }
        
        return result;
    }
    
       
    /**
     * Basically, don't make a losing move 
     * 
     * @return 1 if lost, 0 if win
     */
    public int isLost() {
        if (lost == true) {
            return 1;
        } else {
            return 0;
        }
    }
}

/*****************************************************************************************
 * Uncomment and put the 3 classes Individual, Population, GeneticAlgorithm into separate classes
 * Then switching isLearning in PlayerSkeleton to true, then in main() method, uncomment the learning branch
 * to run the learning process
 *****************************************************************************************/
//
///**
// * 
// * Individual (or state) representation in GA.
// * Using the weight vector, plays the game
// * The fitness function is number of rows cleared after the game.
// */
//public class Individual implements Runnable, Comparable<Individual> {
//
//    //the weight vector
//    private static final int size = PlayerSkeleton.NUM_OF_HEURISTICS;
//    private double[] weight = new double[size];
//
//    //the player and the fitness, after the game runs once.
//    private PlayerSkeleton player;
//    private int fitness = -1;
//
//    /**
//     * Deep copy of the individual
//     * 
//     * @param other - individual
//     */
//    public Individual replicate(Individual other) {
//        Individual replica = new Individual();
//        replica.player = other.player;
//        replica.fitness = other.fitness;
//        for(int i = 0; i < size; ++i) {
//            replica.weight[i] = other.weight[i];
//        }
//        return replica;
//    }
//
//    /**
//     * Generate a random Individual
//     * 
//     * @return random Individual
//     */
//    public Individual generateRandom() {
//        Individual random = new Individual();
//        //the 1st, 3rd, 4th, 5th heuristics should be positive
//        //the 2nd should be negative
//        //WE DON'T TRAIN isLost() heuristic in GA
//        double value;
//        for (int i = 0; i < size-1; i++) {
//            if (i == 1) {
//                value = Math.random() * (-100);
//            } else {
//                value = Math.random() * 100;
//            }
//            weight[i] = value;
//        }
//        weight[size-1] = 999999;
//        player = new PlayerSkeleton();
//        player.setWeightVector(weight);
//        return random;
//    }
//
//    /**
//     * Change the value of the heuristic weight
//     * 
//     * @param i - index
//     * @param value - value to be changed
//     */
//    public void setWeight(int i, double value){
//        weight[i] = value;
//    }
//
//    /**
//     * Get the value of the heuristic weight
//     * 
//     * @param i - index
//     * @return value - value to be changed
//     */
//    public double getWeight(int i){
//        return weight[i];
//    }
//
//    /**
//     * If no game has been run, run it
//     * Or else, just ignore and return the fitness of this individual
//     * 
//     * @return fitness of the individual
//     */
//    public int getFitness() {
//        if (player == null || fitness == -1) { //if the game has not been played, play it
//            player = new PlayerSkeleton();
//            player.setWeightVector(weight);
//            fitness += player.run();
//
//        } 
//        return fitness;
//    }
//
//    /**
//     * Reset the game for this Individual, after 1 generation is done
//     */
//    public void reset() {
//        player = null;
//        fitness = -1;
//    }
//
//    /**
//     * toString method
//     * String representation is the weights
//     */
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            sb.append(weight[i]);
//            sb.append(" ");
//        }
//        return sb.toString();
//    }
//
//    public void importFromFile(String s) {
//        String[] weightString = s.split("\\s+"); //split by whitespace
//        if (weightString.length != size) {
//            throw new IllegalArgumentException("Not the correct String for Weight Vector");
//        }
//        for (int i = 0; i < size; i++) {
//            weight[i] = Double.parseDouble(weightString[i]);
//        }
//        player = new PlayerSkeleton();
//        player.setWeightVector(weight);
//    }
//
//    @Override
//    //Runnable thread method
//    public void run() {
//        getFitness();
//    }
//
//    @Override
//    //comparable method
//    public int compareTo(Individual o) {
//        return this.getFitness() - o.getFitness(); //prevent fitness == -1
//    }
//
//}
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Arrays;
//
///**
// * 
// * Population in GA
// * Manipulate/Maintain a population of Individuals
// */
//public class Population {
//
//    //population size
//    private static final int POPULATION_SIZE = 200;
//    
//    //Arrays of Individuals
//    private Individual[] population;
//    
//    /**
//     * Constructor: create a random population of size 50 (For 1st run)
//     */
//    public Population() {
//        population = new Individual[POPULATION_SIZE];
//        
//        for (int i = 0; i < POPULATION_SIZE; i++) {
//            Individual random = new Individual();
//            random.generateRandom();
//            population[i] = random;
//        }
//    }
//    
//    /**
//     * Constructor: create an empty population of a specific size
//     * (used for tournament selection, or when we need an empty population)
//     * 
//     * @param populationSize - number of individuals
//     */
//    public Population(int populationSize) {
//        population = new Individual[populationSize];
//    }
//    
//    /**
//     * Get the most fit individual
//     * 1. Get fitness for each individual
//     * 2. Return the fittest individual (highest score)
//     * 
//     * We can use multi-threads to play all the games.
//     * @return
//     */
//    public Individual getFittest() {
//        Individual fittest = population[0];
//        int length = population.length; //cannot assume to be 50.
//              
//        Thread[] threads = new Thread[length];
//        
//        //run all threads
//        for (int i = 0; i < length; i++) {
//            threads[i] = new Thread(population[i]);
//            threads[i].start();
//        }
//        
//        //wait for all threads to finish
//        for (int i = 0; i < length; i++) {
//            try {
//                threads[i].join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        //get fittest individual
//        for (int i = 0; i < length; i++) {
//            if (fittest.getFitness() < population[i].getFitness()){
//                fittest = population[i];
//            }
//        }
//        
//        return fittest;
//    }
//    
//    /**
//     * Sort the population by its fitness in increasing order
//     */
//    public void sort() {
//        Arrays.sort(population);
//    }
//    
//    /**
//     * Reset all individuals in the population
//     */
//    public void reset() {
//        for (int i = 0; i < population.length; i++) {
//            population[i].reset();
//        }
//    }
//    
//    /**
//     * Set the individual (setter method)
//     * 
//     * @param individual - to be set
//     * @param i - index to be set at
//     */
//    public void setIndividual(Individual individual, int i) {
//        population[i] = individual;
//    }
//    
//    /**
//     * Export the population to a text file as a String
//     * This is so we can resume learning anytime we want
//     */
//    public void exportToFile(String filePath) {
//        try (PrintWriter out = new PrintWriter(filePath);) {          
//            for (int i = 0; i < population.length; i++) {
//                out.println(population[i].toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    
//    /**
//     * Import the population from a String and initialize the population
//     * This is so we can resume learning anytime we want
//     */
//    public void importFromFile(String filePath) {
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            int i = 0;
//            while ((line = br.readLine()) != null) {
//                Individual individual = new Individual();
//                individual.importFromFile(line);
//                population[i] = individual;
//                i++;
//             }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    
//    /**
//     * Get the individual (getter method)
//     * 
//     * @param i - index to get get from
//     * @return
//     */
//    public Individual getIndividual(int i) {
//        return population[i];
//    }
//    
//    /**
//     * Get size of population (getter method
//     * 
//     * @return size of population
//     */
//    public int getSize() {
//        return population.length;
//    }
//}
///**
// * 
// * Implementation of all algorithms in Genetic algorithm
// * This includes:
// * 1. Selection (We use deterministic tournament selection instead of 
// *                     fitness proportionate or reward-based selection)
// * 2. Crossover (We use uniform crossover instead of single-point or two-point crossover)
// * 3. Mutation (We use uniform mutation scheme instead of bit-flipping)
// * (Reasons may be provided in report)
// * 
// * 4. Piece together the above processes to run GA
// */
//public class GeneticAlgorithm {
//
//    //GA parameters
//
//    private int POPULATION_SIZE = 200;
//    private int CITIZENS = 200;          //number of normal citizens
//    private int FOREIGNERS = 0;         //number of foreigners
//    private int ELITES = 0;             //number of elites  
//    private int TOURNAMENT_SIZE = 10; 
//
//    private double CROSSOVER_RATE = 0.65; //crossover rate
//    private double UNIFORM_RATE = 0.5; //uniform crossover
//    private double MUTATION_RATE = 0.01; //uniform mutation
//
//    //true if first time running, false if you already have an population
//    //from previous run
//    private boolean isFirstTime = true;
//
//    /**
//     * Deterministic Tournament Selection
//     * 
//     * Randomly choose 5 people up for tournament
//     * Return the fittest among them
//     * 
//     * @return fittest in the tournament
//     */
//    public Individual select(Population population) {
//        //Create a new tournament population and fill it with random Individual from population
//        Population tournament = new Population(TOURNAMENT_SIZE);
//
//        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
//            int rand = (int) (Math.random() * population.getSize());
//            tournament.setIndividual(population.getIndividual(rand), i);
//        }
//
//        //return the fittest in the tournament
//        return tournament.getFittest();        
//    }
//
//    /**
//     * Uniform cross-over scheme to cross the features between 2 individuals
//     * However, WE DO NOT CARE about isLost() (last heuristic).
//     * 
//     * @param first - Individual
//     * @param second - Individual
//     */
//    public void crossOver(Individual first, Individual second) {
//
//        if (Math.random() <= CROSSOVER_RATE) {
//            //loop through heuristic features, ignore last one
//            for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
//                //uniform cross-over, swap 2 weights
//                if (Math.random() <= UNIFORM_RATE) {
//                    double temp = first.getWeight(i);
//                    first.setWeight(i, second.getWeight(i));
//                    second.setWeight(i, temp);                
//                }
//            }
//        }
//    }
//
//    /**
//     * Uniform mutation scheme
//     * Again, we don't mutate isLost()
//     * 
//     * @param individual
//     */
//    public void mutate(Individual individual) {
//        //loop through gene and mutate with some 
//        double weight;
//        for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
//            if (Math.random() <= MUTATION_RATE) {
//                if (i == 1) {
//                    weight = Math.random() * (-1000);
//                } else {
//                    weight = Math.random() * 1000;
//                }
//                individual.setWeight(i, weight);
//            }
//        }
//    }
//
//    /**
//     * Piece together the above 3 algorithms: select, crossOver and mutate
//     * to get the next population.
//     * 
//     * In one generations of 50 individuals, we keep the best 10,
//     * eliminate the worst 40, substitute it with 40 tournament winners
//     * (the whole original population gets to participate in the tournament :D)
//     * 
//     * Do we mutate the 10 we keep? No. In real life, there is no mutation if there's no 
//     * *mating* involved
//     * 
//     * @param population - current population
//     * 
//     * @return next generation population
//     */
//    public Population getNextGeneration(Population population) {
//        int size = population.getSize();
//        Population nextPopulation = new Population(size);
//
//        //select and crossover then put them in nextPopulation (first 40)
//        for (int i = 0; i < CITIZENS; i+=2) {
//            Individual first = new Individual();
//            first = first.replicate(select(population));
//
//            Individual second = new Individual();
//            second = second.replicate(select(population));
//
//            crossOver(first, second);
//            nextPopulation.setIndividual(first, i);
//            nextPopulation.setIndividual(second, i+1);
//        }
//
//        //mutation process, we don't mutate foreigners and elites
//        for (int i = 0; i < CITIZENS; i++) {
//            mutate(nextPopulation.getIndividual(i));
//        }
//
//        //foreigners
//        for (int i = CITIZENS; i < CITIZENS + FOREIGNERS; i++) {
//            Individual newGuy = new Individual();
//            newGuy.generateRandom();
//            nextPopulation.setIndividual(newGuy, i);
//        }
//
//        //elites
//        population.sort();
//        for (int i = CITIZENS + FOREIGNERS; i < size; i++) {
//            nextPopulation.setIndividual(population.getIndividual(i), i);
//        }
//
//        nextPopulation.reset();        
//        return nextPopulation;
//    }
//
//
//    /**
//     * Run the learning process
//     */
//    public void learn() {
//        Population population;
//        if (isFirstTime) {
//            //create an initial random population
//            population = new Population();        
//        } else {
//            population = new Population(POPULATION_SIZE);
//            //initilialize population from data.txt. Change the file to the file you want
//            population.importFromFile("data10.txt");
//        }
//
//        int round = 0;
//        final long startTime = System.currentTimeMillis();
//        //run until the fittest in the generation clear 10 million points
//        while (population.getFittest().getFitness() < 1000000) {
//            round++;
//            System.out.println("Round " + round + ": The fittest is ");
//            
//            assert(CITIZENS+FOREIGNERS+ELITES == POPULATION_SIZE);
//            
//            population = getNextGeneration(population);
//            System.out.println(population.getFittest());
//            System.out.println(population.getFittest().getFitness());
//            System.out.println();
//
//            if (round == 5) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            } else if (round == 10) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            } else if (round == 15) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            } else if (round == 20) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            } else if (round == 25) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            } else if (round == 30) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            } else if (round == 35) { 
//                MUTATION_RATE += 0.005;
//                CITIZENS -= 10;
//                FOREIGNERS += 10;
//            }
//            
//            
//            //export to file every 10 rounds, so we can resume later if needed
//            //can change the frequency
//            if (round % 10 == 0) {
//                population.exportToFile("data" + round + ".txt");
//            }
//        }
//
//        final long endTime = System.currentTimeMillis();
//        System.out.println("Total execution time: " + (endTime - startTime) );
//        System.out.println("Total rounds: " + round);
//    }
//}

