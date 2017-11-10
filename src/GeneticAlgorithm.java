import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * @author Trevor Vanderee
 * @date 2017-11-11
 * @class GeneticAlgorithm
 * @StudentNumber 5877022
 * @Username tv15rz
 *
 * Description:
 * This program is used to find an efficient
 * solution to the Travelling Salesman problem.
 * Each solution is represented as a chromosome
 * and is evolved over time to find a better solution.
 */
public class GeneticAlgorithm {

    private double crossoverRate;
    private double mutationRate;

    private long seed;

    private int populationSize;
    private int  max_gen;
    private int numCities;
    private int numElites;
    private int tournamentSize;
    private Random numGen;
    private City[] data;
    private CrossoverType crossover;

    private enum CrossoverType{
        UOX,
        PMX
    }

    private enum ExpectedType{
        _integer,
        _double,
        _long
    }

    public GeneticAlgorithm(){
        init();

        LinkedList<Solution> population = GeneratePopulation(data);

        Evolve(population, 1);
    }


    /**
     * This method generates a random population of solutions
     *
     * @param path
     * @return The randomly generated list of solutions
     */
    private LinkedList<Solution> GeneratePopulation(City[] path){
        LinkedList<Solution> population = new LinkedList<>();
        for(int i = 0; i< populationSize; i++){
            population.add(shuffleArray(path.clone()));
        }
        return population;
    }

    /**
     * This method recursively calls itself for each generation.
     * It applies crossovers and mutations to create new populations.
     *
     * @param population
     * @param Generation
     */
    private void Evolve(LinkedList<Solution> population, int Generation){
        LinkedList<Solution> newPopulation = new LinkedList<>();

        //Add the chosen elites to the next generation
        Solution[] elites = FindElites(population);
        for(int i =0; i< elites.length; i++){
            newPopulation.add(elites[i]);
        }

        //Print out the generations best solution
        System.out.println("Generation " + Generation);
        System.out.println("Elite Solution:");
        printSolution(elites[0]);
        System.out.println("Fitness: " + elites[0].getFitness());
        System.out.println("\n");

        //Populate the new population with new Solutions
        while(newPopulation.size()<populationSize){
            //Select two chromosomes as parents
            Solution selected1 = TournamentSelection(population);
            Solution selected2 = TournamentSelection(population);

            //If probable crossover the parents with the specified crossover type
            if(numGen.nextDouble()<crossoverRate){
                if(crossover == CrossoverType.UOX){
                    Solution[] children = UniformOrderCrossover(selected1, selected2);
                    selected1 = children[0];
                    selected2 = children[1];
                }else if(crossover == CrossoverType.PMX){
                    Solution[] children = PartiallyMappedCrossover(selected1, selected2);
                    selected1 = children[0];
                    selected2 = children[1];
                }
            }

            //If probable mutate the Selected chromosomes
            if(numGen.nextDouble()<mutationRate){
                selected1 = Mutate(selected1);
            }
            if(numGen.nextDouble()<mutationRate) {
                selected2 = Mutate(selected2);
            }

            //Add to the new population
            newPopulation.add(selected1);
            if(newPopulation.size()!=populationSize){
                newPopulation.add(selected2);
            }
        }

        //If we have not yet reached the maximum generation
        if(Generation != max_gen){
            Evolve(newPopulation, ++Generation);
        }
    }

    /**
     * This method finds the elite solutions in a population.
     *
     * @param population
     * @return The selected elite solutions
     */
    private Solution[] FindElites(LinkedList<Solution> population){
        Solution[] elites = new Solution[numElites];

        //Iterate through all solutions
        for(int i =0; i < population.size(); i++){
            Solution attempt = population.get(i);

            //Push into array
            for(int j =0; j<elites.length; j++){
                if(elites[j]!=null){
                    if(attempt.getFitness()<elites[j].getFitness()){
                        Solution temp = elites[j];
                        elites[j] = attempt;
                        attempt = temp;
                    }
                }else{
                    elites[j] = attempt;
                    break;
                }
            }
        }
        return elites;
    }

    /**
     * This method applies a uniform order crossover between two parents
     * to produce two children.
     * @param solution1
     * @param solution2
     * @return The children solutions
     */
    private Solution[] UniformOrderCrossover(Solution solution1, Solution solution2){
        boolean[] bitmask = getBitmask();
        City[] parent1 = solution1.getPath();
        City[] parent2 = solution2.getPath();
        City[] child1 = new City[numCities];
        City[] child2 = new City[numCities];
        LinkedList<City> child1List = new LinkedList<>();
        LinkedList<City> child2List = new LinkedList<>();

        for(int i = 0; i<numCities; i++){
            if(bitmask[i]){

                child1[i] = parent1[i];
                child1List.add(child1[i]);

                child2[i] = parent2[i];
                child2List.add(child2[i]);
            }
        }

        for(int i = 0; i<numCities; i++){
            if(child1[i]== null){
                for(int j=0; j<numCities; j++){
                    City attempt = parent2[j];
                    if(!child1List.contains(attempt)){
                        child1[i]= attempt;
                        child1List.add(attempt);
                        break;
                    }
                }
            }

            if(child2[i]== null){
                for(int j=0; j<numCities; j++){
                    City attempt = parent1[j];
                    if(!child2List.contains(attempt)){
                        child2[i]= attempt;
                        child2List.add(attempt);
                        break;
                    }
                }
            }
        }
        Solution[] result = new Solution[2];
        result[0] = new Solution(child1);
        result[1] = new Solution(child2);
        return result;
    }

    /**
     * This method applies a partial mapping crossover between two parents
     * to produce two children
     * @param solution1
     * @param solution2
     * @return The children solutions
     */
    private Solution[] PartiallyMappedCrossover(Solution solution1, Solution solution2){
        City[] parent1 = solution1.getPath();
        City[] parent2 = solution2.getPath();
        City[] child1 = new City[numCities];
        City[] child2 = new City[numCities];
        LinkedList<City> child1List = new LinkedList<>();
        LinkedList<City> child2List = new LinkedList<>();

        for(int i =0; i<numCities; i++){
            child1List.add(parent2[i]);
            child2List.add(parent1[i]);
        }

        int substringLength = numGen.nextInt(numCities);
        int startingIndex = numGen.nextInt(numCities- substringLength);
        for(int i = startingIndex; i< substringLength; i++){
            child1[i] = parent2[i];
            child1List.remove(parent2[i]);
            child2[i] = parent1[i];
            child2List.remove(parent1[i]);
        }
        for(int i =0; i<numCities; i++){
            if (child1[i]==null){
                City attempt = parent1[i];
                if(child1List.contains(attempt)){
                    child1[i] = attempt;
                    child1List.remove(attempt);
                }
            }
            if (child2[i]==null){
                City attempt = parent2[i];
                if(child2List.contains(attempt)){
                    child2[i] = attempt;
                    child2List.remove(attempt);
                }
            }
        }
        for(int i =0; i<numCities; i++){
            if(child1[i] == null){
                child1[i]= child1List.get(0);
                child1List.remove(0);
            }
            if(child2[i] == null){
                child2[i]= child2List.get(0);
                child2List.remove(0);
            }
        }
        Solution[] solutions = new Solution[2];
        solutions[0] = new Solution(child1);
        solutions[1] = new Solution(child2);
        return solutions;
    }

    /**
     * This method applies a swap mutation on a solution
     * @param solution
     * @return The mutated solution
     */
    private Solution Mutate(Solution solution) {
        City[] path = solution.getPath();
        int index1 = numGen.nextInt(path.length);
        int index2 = numGen.nextInt(path.length);
        City temp = path[index1];
        path[index1] = path[index2];
        path[index2] = temp;
        return new Solution(path);
    }

    /**
     * This method randomly generates a bitmask for the uniform order crossover
     * @return boolean[] The bitmask
     */
    private boolean[] getBitmask(){
        boolean[] bitmask = new boolean[data.length];
        for(int i = 0; i<bitmask.length; i++){
            bitmask[i] = numGen.nextBoolean();
        }
        return bitmask;
    }

    /**
     * This method randomly selects k individuals and returns the best of them
     * @param population
     * @return The selected solution
     */
    private Solution TournamentSelection(LinkedList<Solution> population){
        Solution best = null;
        for(int i =0; i<tournamentSize; i++){

            //Randomly select a solution
            int selection = numGen.nextInt(populationSize);
            Solution selected = population.get(selection);

            //Check if it is better than the last chosen
            if(best!=null){
                if(selected.getFitness()<best.getFitness()){
                    best = selected;
                }
            }else{
                best = selected;
            }
        }
        return best;
    }

    /**
     * This method prints a solutions tour to the console
     * @param solution
     */
    private void printSolution(Solution solution){
        City[] path = solution.getPath();
        String output = "";
        for(int i =0; i<path.length; i++){
            output += path[i].getID() + " ";
        }
        System.out.println(output);
    }

    /**
     * This method randomly shuffles an array for initial population
     * @param array
     * @return The shuffled Solution
     */
    private Solution shuffleArray(City[] array) {
        numGen.nextInt();
        for (int i = 0; i < array.length; i++) {
            int swap = i + numGen.nextInt(array.length - i);
            City temp = array[i];
            array[i] = array[swap];
            array[swap] = temp;
        }
        return new Solution(array);
    }

    /**
     * This method reads in the data from the file provided.
     * @param file
     * @return The cities from the file
     */
    private City[] readData(File file){
        try{
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            int dimension = 0;
            while((str = in.readLine())!= null){
                String[] temp = str.split(":");
                if(temp[0].equals("DIMENSION")){
                    temp[1] = temp[1].trim();
                    dimension= Integer.parseInt(temp[1]);
                }
                if(temp.length == 1){
                    break;
                }
            }
            String[] nodeData;
            int id;
            double x_coord;
            double y_coord;
            City[] cities = new City[dimension];
            int i =0;
            while((str = in.readLine())!= null && !str.equals("EOF")){
                nodeData = str.split(" ");
                id = Integer.parseInt(nodeData[0]);
                x_coord = Double.parseDouble(nodeData[1]);
                y_coord = Double.parseDouble(nodeData[2]);
                cities[i] = new City(id,x_coord,y_coord);
                i++;
            }
            return cities;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This is the initation method used to setup the
     * program for execution.
     */
    private void init() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Use defaults? (Y/N)");
        String response = sc.nextLine();
        if(response.toUpperCase().equals("Y")){

            //Default Values for testing
            String filename = "berlin52.tsp";
            File file = new File("./" + filename);
            data=readData(file);
            numCities = data.length;
            crossoverRate = 1.0;
            mutationRate = 0.1;
            populationSize = 50;
            seed = 345;
            numGen = new Random(seed);
            max_gen = 700;
            crossover = CrossoverType.UOX;
            tournamentSize = 3;
            numElites = 3;
        }else{
            //Manual input

            //File input
            System.out.println("Enter the Filename: ");
            String fileName;
            File file;
            //make sure the file is valid
            while (true) {
                fileName = sc.nextLine();
                file = new File("./" + fileName);
                if (file.exists()) {
                    break;
                } else {
                    System.out.println("Invalid Filename, try again:");
                }
            }
            data = readData(file);
            //Set the number of cities
            numCities = data.length;

            //Crossover Operator
            System.out.println("Crossover operator UOX or PMX (U/P):");
            String crossoverOperator = sc.nextLine();
            if(crossoverOperator.toUpperCase().equals("U")){
                crossover = CrossoverType.UOX;
            }else if (crossoverOperator.toUpperCase().equals("P")){
                crossover = CrossoverType.PMX;
            }else{
                System.out.println("Invalid: Using UOX");
                crossover = CrossoverType.UOX;
            }

            //Crossover Rate
            crossoverRate = (double)readNumberFromConsole(sc,"Crossover Rate:",ExpectedType._double,90)/100;

            //Mutation rate
            mutationRate = (double)readNumberFromConsole(sc,"Mutation Rate:",ExpectedType._double,10)/100;

            //Population Size
            populationSize = (int)readNumberFromConsole(sc,"Population Size:",ExpectedType._integer,50);

            //Tournament Size
            tournamentSize = (int)readNumberFromConsole(sc,"Tournament Size:",ExpectedType._integer,3);

            //Number of elites
            numElites = (int)readNumberFromConsole(sc,"Number of elites:",ExpectedType._integer,2);

            //Value of seed
            seed = (long)readNumberFromConsole(sc,"Seed Value:",ExpectedType._long,789);
            numGen = new Random(seed);

            //Maximum number of generations
            max_gen = (int)readNumberFromConsole(sc,"Maximum number of generations:",ExpectedType._integer,5000);
        }
    }

    /**
     * This method reads and parses a number from console.
     * @param sc
     * @param command
     * @param type
     * @param defaultNumber
     * @return Inputted value or defaultNumber if value was invalid.
     */
    private Number readNumberFromConsole(Scanner sc,String command,ExpectedType type, Number defaultNumber){
        String readString;
        System.out.println(command);
        readString = sc.nextLine();
        try {
            switch(type){
                case _double:
                    return Double.parseDouble(readString);
                case _integer:
                    return Integer.parseInt(readString);
                case _long:
                    return Long.parseLong(readString);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid. Using " + defaultNumber);
            return defaultNumber;
        }
        return defaultNumber;
    }

    public static void main(String args[]){new GeneticAlgorithm();}
}
