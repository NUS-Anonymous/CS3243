/**
 * 
 * Population in GA
 * Manipulate/Maintain a population of Individuals
 */
public class Population {

    //population size
    private static final int POPULATION_SIZE = 50;
    
    //number of heuristic features
    private static final int size = PlayerSkeleton.NUM_OF_HEURISTICS;
    
    //Arrays of Individuals
    private Individual[] population;
    
    /**
     * Constructor: create a random population of size 50 (For 1st run)
     */
    public Population() {
        population = new Individual[POPULATION_SIZE];
        
        for (int i = 0; i < size; i++) {
            Individual random = new Individual();
            random.generateRandom();
            population[i] = random;
        }
    }
    
    /**
     * Constructor: create an empty population of a specific size
     * (used for tournament selection, or when we need an empty population)
     * 
     * @param populationSize - number of individuals
     */
    public Population(int populationSize) {
        population = new Individual[populationSize];
    }
    
    /**
     * Get the most fit individual
     * 1. Get fitness for each individual
     * 2. Return the fittest individual (highest score)
     * 
     * We can use multi-threads to play all the games.
     * @return
     */
    public Individual getFittest() {
        Individual fittest = population[0];
        int length = population.length; //cannot assume to be 50.
              
        Thread[] threads = new Thread[length];
        
        //run all threads
        for (int i = 0; i < length; i++) {
            threads[i] = new Thread(population[i]);
            threads[i].start();
        }
        
        //wait for all threads to finish
        for (int i = 0; i < length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        //get fittest individual
        for (int i = 0; i < length; i++) {
            if (fittest.getFitness() < population[i].getFitness()){
                fittest = population[i];
            }
        }
        
        return fittest;
    }
    
    /**
     * Set the individual (setter method)
     * 
     * @param individual - to be set
     * @param i - index to be set at
     */
    public void setIndividual(Individual individual, int i) {
        population[i] = individual;
    }
    
    /**
     * Get the individual (getter method)
     * 
     * @param i - index to get get from
     * @return
     */
    public Individual getIndividual(int i) {
        return population[i];
    }
    
    /**
     * Get size of population (getter method
     * 
     * @return size of population
     */
    public int getSize() {
        return population.length;
    }
}
