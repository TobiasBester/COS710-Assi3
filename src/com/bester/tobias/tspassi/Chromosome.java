package com.bester.tobias.tspassi;

import java.util.*;
import java.util.stream.IntStream;

public class Chromosome {

    private final int id;
    private List<City> cities;
    private Integer currentFitness = Integer.MAX_VALUE;

    Chromosome(int id, List<City> cities, boolean shuffleCities) {
        this.id = id;
        this.cities = new ArrayList<>(cities);
        if (shuffleCities) {
            randomizeCities();
        }
        evaluate();
    }

    Chromosome(Chromosome copy) {
        this.id = copy.getId();
        this.cities = new ArrayList<>(copy.getCities());
        this.currentFitness = copy.getCurrentFitness();
    }

    Integer evaluate() {
        Optional<Integer> fitness = IntStream.range(0, cities.size() - 2)
                .mapToObj(idx -> findDistanceBetweenCities(cities.get(idx), cities.get(idx + 1)))
                .reduce(Integer::sum)
                .map(sum -> sum + findDistanceBetweenFirstAndLastCity());

        currentFitness = fitness.orElse(Integer.MAX_VALUE);
        return currentFitness;
    }

    int[] getTwoRandomIndexes() {
        Random r = new Random();
        int idx1 = r.nextInt(cities.size());
        int idx2 = r.nextInt(cities.size());
        while (idx1 == idx2) {
            idx2 = r.nextInt(cities.size());
        }

        int[] result = new int[]{idx1, idx2};
        Arrays.sort(result);

        return result;
    }

    private void randomizeCities() {
        Collections.shuffle(this.cities, new Random(new Random().nextInt()));
    }

    private Integer findDistanceBetweenFirstAndLastCity() {
        City firstCity = cities.get(0);
        City lastCity = cities.get(cities.size() - 1);
        return findDistanceBetweenCities(firstCity, lastCity);
    }

    private Integer findDistanceBetweenCities(City city1, City city2) {
        double xd = city1.getX() - city2.getX();
        double yd = city1.getY() - city2.getY();
        return Math.round((float) Math.sqrt((xd * xd) + (yd * yd)));
    }

    @Override
    public String toString() {
        return "\nChromosome{" +
                "id=" + id +
                ", currentFitness=" + currentFitness +
                '}';
    }

    private int getId() {
        return id;
    }

    Integer getCurrentFitness() {
        return currentFitness;
    }

    List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        Collections.copy(cities, this.cities);
    }
}
