import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Shabolda on 4/28/2017.
 */
public class Main {


    static List<Class> _classes = new ArrayList<Class>();
    static List<Centroid> _centroids = new ArrayList<Centroid>();

    public static void main(String[] args) throws IOException {


        _classes = Parser.parse("iris_kmeans/iris.txt");


        System.out.println("Specify the number of K");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;

        while (true) {
            try {
                input = br.readLine();
                int k = Integer.parseInt(input.toString());
                System.out.println("K equals: " + k);
                for (int i = 0; i != k; i++) {
                    int random = ThreadLocalRandom.current().nextInt(0, _classes.size());
                    _centroids.add(new Centroid(_classes.get(random), "C" + Integer.toString(i)));
                }
                clasterize();
                int[] prev_amount = new int[_centroids.size()];
                double[][] prev_percents = new double[_centroids.size()][2];
                for (int i = 0; i != _centroids.size(); i++) {
                    prev_amount[i] = _centroids.get(i)._classes.size();
                    System.out.println(_centroids.get(i)._name + ": " + _centroids.get(i)._classes.size());
                    for (int j = 0; j != 2; j++) {
                        prev_percents[i][j] = _centroids.get(i)._percents[j];
                    }
                    System.out.println(_centroids.get(i)._name + ": " + _centroids.get(i)._percents[0] + "/" + _centroids.get(i)._percents[1] + "/" + _centroids.get(i)._percents[2]);
                }

                boolean if_end = false;
                while (!if_end) {

                    for (int i = 0; i != _centroids.size(); i++) {
                        _centroids.get(i).centralize();
                        _centroids.get(i)._classes.clear();
                    }
                    clasterize();
                    for (int i = 0; i != _centroids.size(); i++) {
                        System.out.println(_centroids.get(i)._name + ": " + _centroids.get(i)._classes.size());
                        System.out.println(_centroids.get(i)._name + ": " + _centroids.get(i)._percents[0] + "/" + _centroids.get(i)._percents[1] + "/" + _centroids.get(i)._percents[2]);
                    }

                    int[] new_amount = new int[_centroids.size()];
                    double[][] new_percents = new double[_centroids.size()][2];

                    for (int i = 0; i != _centroids.size(); i++) {

                        new_amount[i] = _centroids.get(i)._classes.size();
                        for (int j = 0; j != 2; j++) {
                            new_percents[i][j] = _centroids.get(i)._percents[j];
                        }
                    }

                    for (int i = 0; i != _centroids.size()-1; i++) {

                        if (new_amount[i] != prev_amount[i]) {
                            prev_amount = new_amount;
                            prev_percents = new_percents;
                            break;
                        }
                        for (int j = 0; j != 2; j++) {
                            if (new_percents[i][j] != prev_percents[i][j]) {
                                prev_amount = new_amount;
                                prev_percents = new_percents;

                                break;
                            }
                        }
                        if_end = true;
                        _centroids.clear();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public static void clasterize() {

        for (int i = 0; i != _classes.size(); i++) {
            double[] vectors = new double[_centroids.size()];
            for (int k = 0; k != _centroids.size(); k++) {
                vectors[k] = _centroids.get(k).calcVector(_classes.get(i));
            }


            int index = findSmallest(vectors);
            _centroids.get(index).
                    _classes.add(_classes.get(i));
        }
        for (int i = 0; i != _centroids.size(); i++) {

            _centroids.get(i).calcPercentage();
        }

    }

    public static int findSmallest(double array[]) {
        double smallest = 1111110;
        int index = 0;

        for (int i = 0; i != array.length; i++) {
            if (array[i] < smallest) {
                smallest = array[i];
                index = i;
            }
        }

        return index;
    }

}
