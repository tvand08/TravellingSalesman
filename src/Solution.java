/**
 * Created by Trevor Vanderee on 2017-11-07.
 * Solution
 * This class is a representation of a chromosome
 * also known as a solution
 */
public class Solution {

    private City[] path;
    private double fitness;


    public Solution(City[] path){
        this.path = path;
        fitness = fitnessEvaluation();
    }

    /**
     * This method returns the tour of the solution
     * @return Path
     */
    public City[] getPath(){
        return path;
    }

    /**
     * This method returns the fitness of the solution
     * @return Fitness
     */
    public double getFitness(){
        return fitness;
    }

    /**
     * This function evaluates the path and returns the fitness
     * @return Evaluated Fitness
     */
    private double fitnessEvaluation(){
        double yDistance = 0;
        double xDistance =0;
        for(int i =0; i<path.length; i++){
            yDistance += Math.abs(path[i].y_coordinate- path[(i+1)%path.length].y_coordinate);
            xDistance += Math.abs(path[i].x_coordinate- path[(i+1)%path.length].x_coordinate);
        }
        return Math.sqrt(xDistance*xDistance+yDistance*yDistance);
    }

}
