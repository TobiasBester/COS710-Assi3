package com.bester.tobias.tspassi;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.IntStream;

public class Chromosome {

    private List<City> cities;
    private Double currentFitness = Double.POSITIVE_INFINITY;

    public Chromosome(List<City> cities) {
        this.cities = new ArrayList<>(cities);
        randomizeCities();
        evaluate();
    }

    Double evaluate() {
        Optional<Double> fitness = IntStream.range(0, cities.size() - 2)
                .mapToObj(idx -> findDistanceBetweenCities(cities.get(idx), cities.get(idx + 1)))
                .reduce(Double::sum)
                .map(sum -> sum + findDistanceBetweenFirstAndLastCity());

        currentFitness = fitness.orElse(Double.POSITIVE_INFINITY);
        System.out.println(currentFitness);
        return currentFitness;
    }

    private void randomizeCities() {
        Collections.shuffle(this.cities, new Random(new Random().nextInt()));
    }

    private Double findDistanceBetweenFirstAndLastCity() {
        City firstCity = cities.get(0);
        City lastCity = cities.get(cities.size() - 1);
        return findDistanceBetweenCities(firstCity, lastCity);
    }

    private Double findDistanceBetweenCities(City city1, City city2) {
        return Point2D.distance(city1.getX(), city1.getY(), city2.getX(), city2.getY());
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "cities=" + cities +
                ", currentFitness=" + currentFitness +
                '}';
    }
}
