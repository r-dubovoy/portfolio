/**
 * Created by Shabolda on 4/28/2017.
 */
public class Class {

    public double [] _weights = {0, 0, 0, 0};
    public String _name;

    public Class(double [] weights, String name){

        _weights = weights;
        _name = name;
    }

    public Class(double v, double v1, double v2, double v3, String name) {

        _weights[0] = v;
        _weights[1] = v1;
        _weights[2] = v2;
        _weights[3] = v3;
        _name = name;
    }
}
