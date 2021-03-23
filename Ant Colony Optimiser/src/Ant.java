import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Ant implements Callable<Ant> {

    public static final double ALPHA = 0.11;
    public static final double BETA = 9.5;
    private ACO aco;
    private int antNumb;

    private Route route = null;
    static int invalidCityIndex = -1;
    static int numbOfCities = Driver.initialRoute.size();

    public static final double Q = 0.0005;
    public static final double RHO = 0.2;

    public Route getRoute() {
        return route;
    }
    public Ant(ACO aco, int antNumb) {
        this.aco = aco;
        this.antNumb = antNumb;
    }

    public Ant call() throws Exception {

        int oringinatingCityIndex = ThreadLocalRandom.current().nextInt(numbOfCities);
        ArrayList<City> routeCities = new ArrayList<City>(numbOfCities);
        HashMap<String, Boolean> visitedCities = new HashMap<String, Boolean>(numbOfCities);
        IntStream.range(0, numbOfCities).forEach(x -> visitedCities.put(Driver.initialRoute.get(x).getName(), false));
        int numbOfVisitedCities = 0;
        visitedCities.put(Driver.initialRoute.get(oringinatingCityIndex).getName(), true);
        double routeDistance = 0.0;
        int x = oringinatingCityIndex;
        int y = invalidCityIndex;

        if (numbOfVisitedCities != numbOfCities) y = getY(x, visitedCities);

        while(y != invalidCityIndex) {

            routeCities.add(numbOfVisitedCities++, Driver.initialRoute.get(x));
            routeDistance += aco.getDistancesMatrix()[x][y];
            adjustPhermal(x, y, routeDistance);
            visitedCities.put(Driver.initialRoute.get(y).getName(), true);
            x = y;
            if(numbOfVisitedCities != numbOfCities) y = getY(x, visitedCities);
            else {
                y = invalidCityIndex;
            }
            routeDistance += aco.getDistancesMatrix()[x][oringinatingCityIndex];
            routeCities.add(numbOfVisitedCities, Driver.initialRoute.get(x));
            route = new Route(routeCities, routeDistance);
            return this;

        }
        return  this;

    }

    private void adjustPhermal(int x, int y, double distance) {

        Boolean flag = false;
        while (!flag) {
            double currentPhermal = aco.getPeronLevels()[x][y].doubleValue();
            double updatePhermal = (1-RHO)*currentPhermal + Q/distance;
            if(updatePhermal < 0.00) flag = aco.getPeronLevels()[x][y].compareAndSet(0);
            else {
                flag = aco.getPeronLevels()[x][y].compareAndSet(updatePhermal);
            }
        }
    }

    private int getY(int x, HashMap<String, Boolean> visitedCities) {
        int returnY = invalidCityIndex;
        double random = ThreadLocalRandom.current().nextDouble();
        ArrayList<Double> transProbs = getTransProbs(x, visitedCities);
        for (int y = 0; y < numbOfCities; y++) {
            if (transProbs.get(y) > random) {
                returnY = y;
                break;
            } else {
                random -= transProbs.get(y);
            }
        }
        return returnY;
    }

    private ArrayList<Double> getTransProbs(int x, HashMap<String, Boolean> visitedCities) {
        ArrayList<Double> transProbs = new ArrayList<Double>(numbOfCities);
        IntStream.range(0, numbOfCities).forEach(i -> transProbs.add(0.0));
        double denom = getTPDenominator(transProbs, x, visitedCities);
        IntStream.range(0, numbOfCities).forEach(y -> transProbs.set(y, transProbs.get(y)/denom));


        return transProbs;
    }
    private double getTPDenominator(ArrayList<Double> transprobs, int x, HashMap<String, Boolean> visitedCities) {
        double denom = 0.0;

        for(int y = 0; y < numbOfCities; y++) {
            if(!visitedCities.get(Driver.initialRoute.get(y).getName())) {
                if (x == y) transprobs.set(y, 0.0);
                else transprobs.set(y, getTPNum(x,y));
                denom += transprobs.get(y);
            }
        }
        return  denom;
    }

    private double getTPNum(int x, int y) {
        double numerator = 0.0;
        double phermoneLevel = aco.getPeronLevels()[y][x].doubleValue();
        if (phermoneLevel != 0.0) {
            numerator = Math.pow(phermoneLevel,ALPHA) * Math.pow(1/aco.getDistancesMatrix()[x][y], BETA);
        }

        return  numerator;
    }


    public int getAntNumb() {
        return antNumb;
    }
}
