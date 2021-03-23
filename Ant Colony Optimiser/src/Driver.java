import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Driver {
    static final int NUMBER_OF_ANTS = 500; // these ants will be used to find the shortest route
    static final double PROCESSING_CYCLE_PROBABILITY = 0.2;

    static ArrayList<City> initialRoute = new ArrayList<City>(Arrays.asList(

            new City("A", 42.3601, -71.0567),
            new City("B", 29.7601, -95.3698),
            new City("C", 30.2601, -97.5567),
            new City("D", 37.7701, -122.5673),
            new City("A", 39.3475, -104.5123),
            new City("E", 41.3653, -118.6347),
            new City("F", 40.3612, -87.0567),
            new City("G", 67.3612, -74.0567)

    ));

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // this is an executor that has many methods to execute many multiple tasks. Also helps to tracking tasks
    static ExecutorCompletionService<Ant> executorCompletionService = new ExecutorCompletionService<Ant>(executorService); // this executes the tasks using the take methods
    private Route shortestRoute = null;

    private int activeAnts = 0; // counter for active ants

    public static void main(String[] args) throws IOException {

        System.out.println("> " + NUMBER_OF_ANTS + " Artificial Ants ...");
        Driver driver = new Driver();
        //driver.printHeading();
        ACO aco = new ACO();
        IntStream.range(1, NUMBER_OF_ANTS).forEach(x -> {
            //System.out.println("> Driver.main: executorCompletionServer.submit(new Ant())");
            executorCompletionService.submit(new Ant(aco, x));
            driver.activeAnts++;


            if (Math.random() > PROCESSING_CYCLE_PROBABILITY) driver.processAnts();

        });
        System.out.println("\n> Driver.main: exit activate ant loop... ");
        driver.processAnts();
        executorService.shutdown();
        System.out.println("\n Optimal Route : " + Arrays.toString(driver.shortestRoute.getCities().toArray()));
        System.out.println("/w Distance     : " + driver.shortestRoute.getDistance());
    }

    private void processAnts() {
        while (activeAnts > 0) {
            //System.out.println("Driver.main: executorCompletionService.take()");
            try {
                Ant ant = executorCompletionService.take().get();
                Route currentRoute = ant.getRoute();
                if (shortestRoute == null || currentRoute.getDistance() < shortestRoute.getDistance()) {
                    shortestRoute = currentRoute;
                    StringBuffer distance = new StringBuffer("     " + String.format("%.2f", currentRoute.getDistance()));
                    IntStream.range(0, 21 - distance.length()).forEach(k -> distance.append(" "));
                    System.out.println(Arrays.toString(shortestRoute.getCities().toArray()) + " |" + distance + "| " + ant.getAntNumb());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            activeAnts--;
        }
    }


//    private void printHeading() {
//        String headingCol1 = "Route";
//        String remainingHeadingCols = "Distance (in Miles) | ant #";
//        int cityNameLength = 0;
//        for (int x = 0; x < initialRoute.size(); x++) cityNameLength += initialRoute.get(x).getName().length();
//        int arrayLength = cityNameLength + initialRoute.size()*2;
//        int partailLength = (arrayLength - headingCol1.length())/2;
//        for (int x = 0; x < partailLength; x++) System.out.println(" ");
//        System.out.println(headingCol1);
//        for (int x = 0; x < partailLength; x++) System.out.println(" ");
//        if((arrayLength % 2) == 0)System.out.println(" ");
//
//        System.out.println(" | " + remainingHeadingCols);
//        cityNameLength += remainingHeadingCols.length() + 3;
//        for (int x = 0; x < cityNameLength + initialRoute.size()*2; x++) System.out.println("-");
//        System.out.println(" ");
//
//    }

}
