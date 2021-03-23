import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class ACO {
    private AtomicDouble[][] phermoneLevels = null;
    private double[][] distancesMatrix = null;

    private ArrayList<City> cities = Driver.initialRoute;
    private int citiesSize = Driver.initialRoute.size();

    public ACO() throws IOException {
        initializeDistances();
        initializePhermone();
    }

    public AtomicDouble[][] getPeronLevels() {
        return phermoneLevels;
    }

    public double[][] getDistancesMatrix() {
        return distancesMatrix;
    }

    private void initializeDistances() throws IOException {
        distancesMatrix = new double[citiesSize][citiesSize];
        IntStream.range(0,citiesSize).forEach(x -> {

            City cityY = cities.get(x);
            IntStream.range(0, citiesSize).forEach(y -> distancesMatrix[x][y] = cityY.measureDistance(cities.get(y)));

        });
    }

    private void initializePhermone() {
        phermoneLevels = new AtomicDouble[citiesSize][citiesSize];
        Random random = new Random();
        IntStream.range(0, citiesSize).forEach(x -> {
            IntStream.range(0, citiesSize).forEach(y -> phermoneLevels[x][y] = new AtomicDouble(random.nextDouble()));
        });
    }


}
