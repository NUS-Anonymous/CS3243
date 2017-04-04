/**
 * 
 * Individual (or state) representation in GA.
 * Using the weight vector, plays the game
 * The fitness function is number of rows cleared after the game.
 */
public class Individual implements Runnable {

    //the weight vector
    private static final int size = PlayerSkeleton.NUM_OF_HEURISTICS;
    private double[] weight = new double[size];

    //the player and the fitness, after the game runs once.
    private PlayerSkeleton player;
    private int fitness = -1;

    /**
     * Deep copy of the individual
     * 
     * @param other - individual
     */
    public Individual replicate(Individual other) {
        Individual replica = new Individual();
        replica.player = other.player;
        replica.fitness = other.fitness;
        for(int i = 0; i < size; ++i) {
            replica.weight[i] = other.weight[i];
        }
        return replica;
    }

    /**
     * Generate a random Individual
     * 
     * @return random Individual
     */
    public Individual generateRandom() {
        Individual random = new Individual();
        //the 1st, 3rd, 4th, 5th heuristics should be positive
        //the 2nd should be negative
        //WE DON'T TRAIN isLost() heuristic in GA
        double value;
        for (int i = 0; i < size-1; i++) {
            if (i == 1) {
                value = Math.random() * (-50);
            } else {
                value = Math.random() * 50;
            }
            weight[i] = value;
        }
        weight[size-1] = 5000;
        player = new PlayerSkeleton();
        player.setWeightVector(weight);
        return random;
    }

    /**
     * Change the value of the heuristic weight
     * 
     * @param i - index
     * @param value - value to be changed
     */
    public void setWeight(int i, double value){
        weight[i] = value;
    }

    /**
     * Get the value of the heuristic weight
     * 
     * @param i - index
     * @return value - value to be changed
     */
    public double getWeight(int i){
        return weight[i];
    }

    /**
     * If no game has been run, run it
     * Or else, just ignore and return the fitness of this individual
     * 
     * @return fitness of the individual
     */
    public int getFitness() {
        if (player == null || fitness == -1) { //if the game has not been played, play it
            player = new PlayerSkeleton();
            player.setWeightVector(weight);
            fitness = player.run();
        } 
        return fitness;
    }

    /**
     * Reset the game for this Individual, after 1 generation is done
     */
    public void reset() {
        player = null;
        fitness = -1;
    }

    @Override
    //Runnable thread method
    public void run() {
        getFitness();
    }

}
