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
    private static final int TOURNAMENT_NUMBERS = 40; //Run 40 tournaments
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
    
    /**
     * Piece together the above 3 algorithms: select, crossOver and mutate
     * to get the next population.
     * 
     * In one generations of 50 individuals, we keep the best 10,
     * eliminate the worst 40, substitute it with 40 tournament winners
     * (the whole original population gets to participate in the tournament :D)
     * 
     * Do we mutate the 10 we keep? No. In real life, there is no mutation if there's no 
     * *mating* involved
     * 
     * @param population - current population
     * 
     * @return next generation population
     */
    public Population getNextGeneration(Population population) {
        int size = population.getSize();
        Population nextPopulation = new Population(size);
        
        //select and crossover then put them in nextPopulation (first 40)
        for (int i = 0; i < TOURNAMENT_NUMBERS; i+=2) {
            Individual first = new Individual();
            first = first.replicate(select(population));
            
            Individual second = new Individual();
            second = second.replicate(select(population));
            
            crossOver(first, second);
            nextPopulation.setIndividual(first, i);
            nextPopulation.setIndividual(second, i+1);
        }
        
        //get the 10 fittest from the original population to put in next population
        population.sort();
        for (int i = TOURNAMENT_NUMBERS; i < size; i++) {
            nextPopulation.setIndividual(population.getIndividual(i), i);
        }
        
        //mutation process, do not mutate the last 10 from original population
        for (int i = 0; i < TOURNAMENT_NUMBERS; i++) {
            mutate(nextPopulation.getIndividual(i));
        }
        
        return nextPopulation;
    }
    
    /**
     * Run the learning process
     */
    public void learn() {
        //create an initial random population
        Population population = new Population();
        
        //run until the fittest in the generation clear 10 million points
        while (population.getFittest().getFitness() < 10000000) {
            population = getNextGeneration(population);
            System.out.println(population.getFittest());
        }
    }
}
