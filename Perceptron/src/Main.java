/**
 * Created by R. Dubovyi on 3/24/2017.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {

    static List<Class> _train_classes = new ArrayList<Class>();
    static List<Class> _test_classes = new ArrayList<Class>();



    public static void main(String[] args) throws IOException {

        /**
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        System.out.print("specify learning parameter: ");
        input = br.readLine();

        System.out.print("specify training error threshold : ");
        input = br.readLine();

        System.out.print("specify number of iterations : ");
        input = br.readLine();

        br.close();
        **/

        Perceptron perceptron = new Perceptron(0.02, 0, 0, 0, 0, 0);
        parse("iris_perceptron/training.txt", _train_classes);
        parse("iris_perceptron/test.txt", _test_classes);

        perceptron.train(_train_classes, 100);
        perceptron.test(_test_classes);

        System.out.println("Specify class for test in format: x1 x2 x3 x4 ");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;

        while(true) {
            try {
                input = br.readLine();
                String[] parts = input.split(",");
                perceptron.think_single(Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void parse(String file_name, List<Class> list) throws IOException {

        FileReader fileReader = new FileReader(file_name);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(",");
            list.add(new Class(Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), parts[4]));
        }
    }
}
