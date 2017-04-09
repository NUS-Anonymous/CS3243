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

    private int POPULATION_SIZE = 200;
    private int CITIZENS = 100;          //number of normal citizens
    private int ELITES = 100;             //number of elites  
    private int TOURNAMENT_SIZE = 15; 

    private double MUTATION_RATE = 0.04; //uniform mutation

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
    public Individual selectAndCrossover(Population population) {
        //Create a new tournament population and fill it with random Individual from population
        Population tournament = new Population(TOURNAMENT_SIZE);

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int rand = (int) (Math.random() * population.getSize());
            tournament.setIndividual(population.getIndividual(rand), i);
        }
        
        Individual first = tournament.getFittest();
        int a = first.getFitness();
        Individual second = tournament.getSecondFittest();
        int b = second.getFitness();
        
        Individual child = new Individual();
        
        double x;
        for (int i = 0; i < PlayerSkeleton.NUM_OF_HEURISTICS-1; i++) {
            x = (a * first.getWeight(i) + b * second.getWeight(i)) / (a+b);
            child.setWeight(i, x);
        }
        child.setWeight(5, 99999);

        //return the fittest in the tournament
        return child;        
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
        for (int i = 0; i < CITIZENS; i++) {
            Individual individual = new Individual();
            individual = selectAndCrossover(population);
            //mutation
            mutate(individual);
            
            nextPopulation.setIndividual(individual, i);
        }

        //elites
        population.sort();
        for (int i = CITIZENS; i < size; i++) {
            nextPopulation.setIndividual(population.getIndividual(i), i);
        }
        
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
            
            assert(CITIZENS+ELITES == POPULATION_SIZE);
            
            population = getNextGeneration(population);
            System.out.println(population.getFittest());
            System.out.println(population.getFittest().getFitness());
            System.out.println();

            //export to file every 10 rounds, so we can resume later if needed
            //can change the frequency
            //if (round % 10 == 0) {
                population.exportToFile("data" + round + ".txt");
            //}
        }

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) );
        System.out.println("Total rounds: " + round);
    }
}
