import java.util.List;

/**
 * Created by R. Dubovyi on 3/24/2017.
 */

public class Perceptron {

    double _yes;
    double _no;
    double _alpha;
    double [] _w;
    double _threshold;

    public Perceptron(double alpha, double threshold, double w1, double w2, double w3, double w4){

        _alpha = alpha;
        _w = new double[]{w1, w2, w3, w4};
        _threshold = threshold;
    }

    public void train(List<Class> Classes, int iterations) {

        for(int k = 0; k != iterations; k++) {
            int local_no = 0;
            int local_yes = 0;
            for (int i = 0; i != Classes.size(); i++) {


                if (think(Classes.get(i)) == 0) {
                    local_no++;
                } else {
                    local_yes++;
                }
                if(k == iterations-1){

                    //System.out.println(_w[0] + " " + _w[1] + " " + _w[2] + " " + _w[3] + " ");
                }
            }
            System.out.println("yes: " + local_yes + "  no: " + local_no);

            _alpha -= 0.0002;
            if(_alpha < 0){
                _alpha = 0.000000001;
            }

        }
    }


    public void test(List<Class> test_classes) {

        for(int i = 0; i != test_classes.size(); i++){

            if(think_test(test_classes.get(i)) == 0){

                _no++;
            }
            else{

                _yes++;
            }

        }
        System.out.println("test accuracy: " + _yes/(_yes+_no) * 100 + "%");
    }

    public int think(Class Class){
        int answer;
        int desired;
        double [] x = {Class._attribute1, Class._attribute2, Class._attribute3, Class._attribute4};
        if(Class._name.equals("Iris-virginica")) {
            desired = 1;
        }
        else{
            desired = 0;
        }
        double res = _w[0] * x[0] + _w[1] * x[1] + _w[2] * x[2] + _w[3] * x[3];
        if( res >= _threshold){


            answer = 1;
        }
        else {

            answer = 0;

        }

        if(answer != desired){
            _w[0] = _w[0] + (desired  - answer)*_alpha*x[0];
            _w[1] = _w[1] + (desired  - answer)*_alpha*x[1];
            _w[2] = _w[2] + (desired  - answer)*_alpha*x[2];
            _w[3] = _w[3] + (desired  - answer)*_alpha*x[3];

            _threshold = _threshold - (desired - answer)*_alpha;

            return 0;
        }
        else {

            return 1;
        }
    }

    public int think_test(Class Class){

        int answer;
        int desired;
        double [] x = {Class._attribute1, Class._attribute2, Class._attribute3, Class._attribute4};
        if(Class._name.equals("Iris-virginica")) {
            desired = 1;
        }
        else{
            desired = 0;
        }
        double res = _w[0] * x[0] + _w[1] * x[1] + _w[2] * x[2] + _w[3] * x[3];
        if( res >= _threshold){


            answer = 1;
        }
        else {

            answer = 0;

        }

        if(answer != desired){

            return 0;
        }
        else {

            return 1;
        }
    }

    public void think_single(double x1, double x2, double x3, double x4){

        double [] x = {x1, x2, x3, x4};

        double res = _w[0] * x[0] + _w[1] * x[1] + _w[2] * x[2] + _w[3] * x[3];
        if( res >= _threshold){

            System.out.println("Iris-virginica");
        }
        else {

            System.out.println("Iris-versicolor");
        }

    }

}
