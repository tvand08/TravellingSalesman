import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by Trevor Vanderee on 2017-11-01.
 */
public class GeneticAlgorithm {
    private double crossoverRate;
    private double mutationRate;
    public GeneticAlgorithm(){
        System.out.println("Enter the Filename: ");
        Scanner sc = new Scanner(System.in);
        String fileName;
        File file;
        while(true){
            fileName = sc.nextLine();

            file = new File("./" + fileName);
            if(file.exists()){
                break;
            }else{
                System.out.println("Invalid Filename, try again:");
            }
        }
        City[] path = readData(file);
        System.out.println("Enter crossover rate:");
        String crossoverRateString = sc.nextLine();
        try{
            crossoverRate = Double.parseDouble(crossoverRateString)/100;
        }catch(NumberFormatException  e){
            System.out.println("Invalid Format. Using Crossover Rate: 90%");
        }
        System.out.println("Enter mutation rate:");
        String mutationRateString = sc.nextLine();
        try{
            mutationRate = Double.parseDouble(mutationRateString)/100;
        }catch(NumberFormatException e){
            System.out.println("Invalid Format. Using Mutation Rate: 90%");
        }



        System.out.println(fitnessEvaluation(path));
    }

    private double fitnessEvaluation(City[] path){
        double yDistance = 0;
        double xDistance =0;
        for(int i =0; i<path.length; i++){
            yDistance += Math.abs(path[i].y_coordinate- path[(i+1)%path.length].y_coordinate);
            xDistance += Math.abs(path[i].x_coordinate- path[(i+1)%path.length].x_coordinate);
        }
        return Math.sqrt(xDistance*xDistance+yDistance*yDistance);
    }

    private City[] readData(File file)
    {
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



    public static void main(String args[]){new GeneticAlgorithm();}
}
