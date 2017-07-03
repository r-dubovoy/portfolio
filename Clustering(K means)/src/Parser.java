import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shabolda on 4/28/2017.
 */
public class Parser {

    public static List<Class> parse(String path) throws IOException {

        List<Class> _classes = new ArrayList<Class>();

        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
            String[] parts = line.split(",");
            if (parts.length == 5) {
                _classes.add(new Class(Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), parts[4]));
            }
        }

        return _classes;
    }
}
