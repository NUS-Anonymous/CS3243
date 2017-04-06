import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 
 * Population in GA
 * Manipulate/Maintain a population of Individuals
 */
public class Population {

    //population size
    private static final int POPULATION_SIZE = 50;
    
    //Arrays of Individuals
    private Individual[] population;
    
    /**
     * Constructor: create a random population of size 50 (For 1st run)
     */
    public Population() {
        population = new Individual[POPULATION_SIZE];
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
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
     * Sort the population by its fitness in increasing order
     */
    public void sort() {
        Arrays.sort(population);
    }
    
    /**
     * Reset all individuals in the population
     */
    public void reset() {
        for (int i = 0; i < population.length; i++) {
            population[i].reset();
        }
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
     * Export the population to a text file as a String
     * This is so we can resume learning anytime we want
     */
    public void exportToFile(String filePath) {
        try (PrintWriter out = new PrintWriter(filePath);) {          
            for (int i = 0; i < population.length; i++) {
                out.println(population[i].toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Import the population from a String and initialize the population
     * This is so we can resume learning anytime we want
     */
    public void importFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                Individual individual = new Individual();
                individual.importFromFile(line);
                population[i] = individual;
                i++;
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
