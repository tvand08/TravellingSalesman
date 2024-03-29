/**
 * Created by Trevor Vanderee on 2017-11-06.
 *
 * The class represents a point on the map.
 * Contains the id and the coordinates
 */
public class City {

    public int ID;
    public double x_coordinate;
    public double y_coordinate;

    public City(int ID, double x_coordinate, double y_coordinate){
        this.ID = ID;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }

    public int getID(){
        return ID;
    }
}
