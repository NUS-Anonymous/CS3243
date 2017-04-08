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

    private int POPULATION_SIZE = 100;
    private int CITIZENS = 100;          //number of normal citizens
    private int FOREIGNERS = 0;         //number of foreigners
    private int ELITES = 0;             //number of elites  
    private int TOURNAMENT_SIZE = 3; 

    private double CROSSOVER_RATE = 0.65; //crossover rate
    private double UNIFORM_RATE = 0.5; //uniform crossover
    private double MUTATION_RATE = 0.015; //uniform mutation

    //true if first time running, false if you already have an population
    //from previous run
    private boolean isFirstTime = true;

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

        if (Math.random() <= CROSSOVER_RATE) {
            //loop through heuristic features, ignore last one
            for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
                //uniform cross-over, swap 2 weights
                if (Math.random() <= UNIFORM_RATE) {
                    double temp = first.getWeight(i);
                    first.setWeight(i, second.getWeight(i));
                    second.setWeight(i, temp);                
                }
            }
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
        for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
            if (Math.random() <= MUTATION_RATE) {
                if (i == 1) {
                    weight = Math.random() * (-1000);
                } else {
                    weight = Math.random() * 1000;
                }
                individual.setWeight(i, weight);
            }
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
        for (int i = 0; i < CITIZENS; i+=2) {
            Individual first = new Individual();
            first = first.replicate(select(population));

            Individual second = new Individual();
            second = second.replicate(select(population));

            crossOver(first, second);
            nextPopulation.setIndividual(first, i);
            nextPopulation.setIndividual(second, i+1);
        }

        //mutation process, we don't mutate foreigners and elites
        for (int i = 0; i < CITIZENS; i++) {
            mutate(nextPopulation.getIndividual(i));
        }

        //foreigners
        for (int i = CITIZENS; i < CITIZENS + FOREIGNERS; i++) {
            Individual newGuy = new Individual();
            nextPopulation.setIndividual(newGuy.generateRandom(), i);
        }

        //elites
        population.sort();
        for (int i = CITIZENS + FOREIGNERS; i < size; i++) {
            nextPopulation.setIndividual(population.getIndividual(i), i);
        }

        nextPopulation.reset();        
        return nextPopulation;
    }


    /**
     * Run the learning process
     */
    public void learn() {
        Population population;
        if (isFirstTime) {
            //create an initial random population
            population = new Population();        
        } else {
            population = new Population(POPULATION_SIZE);
            //initilialize population from data.txt. Change the file to the file you want
            population.importFromFile("data10.txt");
        }

        int round = 0;
        final long startTime = System.currentTimeMillis();
        //run until the fittest in the generation clear 10 million points
        while (population.getFittest().getFitness() < 10000000) {
            round++;
            System.out.println("Round " + round + ": The fittest is ");
            
            assert(CITIZENS+FOREIGNERS+ELITES == POPULATION_SIZE);
            
            population = getNextGeneration(population);
            System.out.println(population.getFittest());
            System.out.println(population.getFittest().getFitness());
            System.out.println();

            //export to file every 10 rounds, so we can resume later if needed
            //can change the frequency
            if (round % 10 == 0) {
                population.exportToFile("data" + round + ".txt");
            }
        }

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );
        System.out.println("Total rounds: " + round);
    }
}
