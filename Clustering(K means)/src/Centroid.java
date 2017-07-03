import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shabolda on 4/28/2017.
 */

public class Centroid {

    double[] _weights;
    String _name;
    List<Class> _classes = new ArrayList<Class>();
    double[] _percents = {0,0,0};

    public Centroid(double[] weights, String name) {

        _weights = weights;
        _name = name;
    }

    public Centroid(Class _class, String name) {

        _weights = _class._weights;
        _name = name;
        System.out.println("Created: " + _name + " " + _weights[0] + ", " + _weights[1] + ", " + _weights[2] + ", " + _weights[3]);

    }

    public void centralize() {
        for (int j = 0; j != _weights.length; j++) {
            _weights[j] = 0;
            for (int i = 0; i != _classes.size(); i++) {
                _weights[j] += _classes.get(i)._weights[j];
            }
            _weights[j] = _weights[j] / _classes.size();
        }
    }

    public double calcVector(Class _class) {

        double vector = 0;
        for (int i = 0; i != _class._weights.length; i++) {
            vector += Math.abs(_weights[i] - _class._weights[i]);
        }
        return vector;
    }

    public void printClasses() {
        System.out.println("!!!!!!!!!!!" + _name + "!!!!!!!!!!!!");
        for (int i = 0; i != _classes.size(); i++) {
            System.out.println(_classes.get(i)._name + ", " + _classes.get(i)._weights[0] + "," + _classes.get(i)._weights[1] + "," + _classes.get(i)._weights[2] + "," + _classes.get(i)._weights[3]);
        }
    }



    public void calcPercentage() {

        double virginica = 0;
        double versicolor = 0;
        double setosa = 0;

        for(int i  = 0; i != _classes.size(); i++){

            if(_classes.get(i)._name.equals("Iris-virginica")){
                virginica++;
            }
            else if(_classes.get(i)._name.equals("Iris-versicolor")){
                versicolor++;
            }
            else{
                setosa++;
            }

        }


        _percents[0] = virginica;
        _percents[1] = versicolor;
        _percents[2] = setosa;



        _percents[0] = (virginica/(_classes.size()) * 100);
        _percents[1] = (versicolor/(_classes.size()) * 100);
        _percents[2] = (setosa/(_classes.size()) * 100);

    }
}
