/**
 * 
 * Implementation of all algorithms in Genetic algorithm
 * This includes:
 * 1. Selection (We use deterministic tournament selection instead of 
 *                     fitness proportionate or reward-based selection)
 * 2. Crossover (We use uniform crossover instead of single-point or two-point crossover)
 * 3. Mutation (We use uniform mutation scheme instead of bit-flipping)
 * (Reasons may be provided in report)
 * 
 * 4. Piece together the above processes to run GA
 */
public class GeneticAlgorithm {
    
    //GA parameters
    private static final int TOURNAMENT_NUMBERS = 30; //Run 30 tournaments
    private static final int TOURNAMENT_SIZE = 5; //5 individuals are chosen for each tournament
    private static final double UNIFORM_RATE = 0.5; //uniform crossover
    private static final double MUTATION_RATE = 0.05; //uniform mutation
    
    /**
     * Deterministic Tournament Selection
     * 
     * Randomly choose 5 people up for tournament
     * Return the fittest among them
     * 
     * @return fittest in the tournament
     */
    public Individual select(Population population) {
        //Create a new tournament population and fill it with random Individual from population
        Population tournament = new Population(TOURNAMENT_SIZE);
        
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int rand = (int) (Math.random() * population.getSize());
            tournament.setIndividual(population.getIndividual(rand), i);
        }
        
        //return the fittest in the tournament
        return tournament.getFittest();        
    }
    
    /**
     * Uniform cross-over scheme to cross the features between 2 individuals
     * However, WE DO NOT CARE about isLost() (last heuristic).
     * 
     * @param first - Individual
     * @param second - Individual
     */
    public void crossOver(Individual first, Individual second) {
        boolean flag = false; //has the individual been cross-over'ed
        
        //loop through heuristic features, ignore last one
        for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
            //uniform cross-over, swap 2 weights
            if (Math.random() <= UNIFORM_RATE) {
                double temp = first.getWeight(i);
                first.setWeight(i, second.getWeight(i));
                second.setWeight(i, temp);
                flag = true;
            }
        }
        //reset the old players, if they have been cross-over'ed
        if (flag) {
            first.reset();
            second.reset();
        }
    }
    
    /**
     * Uniform mutation scheme
     * Again, we don't mutate isLost()
     * 
     * @param individual
     */
    public void mutate(Individual individual) {
        //loop through gene and mutate with some 
        double weight;
        boolean flag = false; //has the individual been mutated
        for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
            if (Math.random() <= MUTATION_RATE) {
                if (i == 1) {
                    weight = Math.random() * (-100);
                } else {
                    weight = Math.random() * 100;
                }
                individual.setWeight(i, weight);
                flag = true;
            }
        }
        
        //reset the old player, if it has been mutated
        if (flag) {
            individual.reset();
        }
    }
}
