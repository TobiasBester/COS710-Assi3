package com.bester.tobias.tspassi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Population {

    private String problemName;
    private String comment;
    private int dimension;
    private int numChromosomes;
    private int numIterations;
    private List<String> coords;
    private List<City> cities;
    private List<Chromosome> chromosomes;

    Population(String problemName, int numChromosomes, int numIterations, boolean directRepresentation) {
        this.problemName = problemName;
        this.numChromosomes = numChromosomes;
        this.numIterations = numIterations;
        this.chromosomes = new ArrayList<>();
        readDataFromFile(problemName);
        saveCities();
    }

    void startSearch() {
        System.out.println("Starting search");
        IntStream.range(0, numIterations).forEach(idx -> {
            System.out.println(String.format("Starting iteration %d", idx));
            evaluatePopulation();
        });
    }

    private void evaluatePopulation() {
        IntStream.range(0, chromosomes.size()).forEach(cIdx -> {
            System.out.println(String.format("Evaluating Chromosome %d", cIdx));
            chromosomes.get(cIdx).evaluate();
        });
    }

    void createInitialPopulation() {
        System.out.println(String.format("Creating Initial Population of %d chromosomes", numChromosomes));
        IntStream.range(0, numChromosomes).forEach(cIdx -> {
            System.out.println(String.format("Creating chromosome number %d", cIdx));
            chromosomes.add(new Chromosome(cities));
        });
    }

    private void saveCities() {
        cities = new ArrayList<>();

        coords.stream()
                .map(string -> string.split(" "))
                .map(stringList -> Arrays.stream(stringList)
                        .filter(string -> string.length() > 0)
                        .collect(Collectors.toList()))
                .forEach(stringList -> cities.add(
                        new City(stringList.get(0), stringList.get(1), stringList.get(2)))
                );
    }

    private void readDataFromFile(String problemName) {
        String fileName = String.format("problems/%s.tsp", problemName);
        Path path = Paths.get(fileName);

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                if (line.startsWith("COMMENT")) {
                    comment = line.substring(8);
                }
                if (line.startsWith("DIMENSION")) {
                    dimension = Integer.parseInt(Arrays.stream(line.split(" ")).reduce((a, b) -> b).get());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<String> lines = Files.lines(path)) {
            List<String> coordsString = lines.collect(Collectors.toList());
            coords = coordsString.subList(6, coordsString.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%nName: %s%nComment: %s%nDimensions: %d%nCities: %s%n",
                problemName, comment, dimension, cities
        );
    }
}
