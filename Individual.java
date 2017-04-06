/**
 * 
 * Individual (or state) representation in GA.
 * Using the weight vector, plays the game
 * The fitness function is number of rows cleared after the game.
 */
public class Individual implements Runnable, Comparable<Individual> {

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
            //get average after 2 games to minimize the case of extreme luck or bad luck
            int fitness1 = player.run();
            int fitness2 = player.run();
            fitness = (fitness1 + fitness2) / 2;
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
    
    /**
     * toString method
     * String representation is the weights
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(weight[i]);
            sb.append(" ");
        }
        return sb.toString();
    }
    
    public void importFromFile(String s) {
        String[] weightString = s.split("\\s+"); //split by whitespace
        if (weightString.length != size) {
            throw new IllegalArgumentException("Not the correct String for Weight Vector");
        }
        for (int i = 0; i < size; i++) {
            weight[i] = Double.parseDouble(weightString[i]);
        }
        player = new PlayerSkeleton();
        player.setWeightVector(weight);
    }

    @Override
    //Runnable thread method
    public void run() {
        getFitness();
    }

    @Override
    //comparable method
    public int compareTo(Individual o) {
        return this.getFitness() - o.getFitness(); //prevent fitness == -1
    }

}
