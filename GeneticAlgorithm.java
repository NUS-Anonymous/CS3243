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
}
