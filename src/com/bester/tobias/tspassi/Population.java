package com.bester.tobias.tspassi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    void createInitialPopulation() {
        System.out.println(String.format("Creating Initial Population of %d chromosomes", numChromosomes));
        IntStream.range(0, numChromosomes).forEach(cIdx -> {
            System.out.println(String.format("Creating chromosome number %d", cIdx));
            chromosomes.add(new Chromosome(cIdx, cities));
        });
    }

    void startSearch(int tournamentSize) {
        System.out.println("Starting search");
        IntStream.range(0, numIterations).forEach(idx -> {
            System.out.println(String.format("Starting iteration %d", idx));
            evaluatePopulation();
            List<Chromosome> parents = selectParents(tournamentSize);
            System.out.println("Parents");
            System.out.println(parents);
            applyOperators();
        });
    }

    private void evaluatePopulation() {
        IntStream.range(0, chromosomes.size()).forEach(cIdx -> {
            System.out.println(String.format(
                    "Evaluating Chromosome %d: %f",
                    chromosomes.get(cIdx).getId(),
                    chromosomes.get(cIdx).evaluate()));
            chromosomes.get(cIdx).evaluate();
        });
    }

    private List<Chromosome> selectParents(int tSize) {

        return IntStream.range(0, chromosomes.size())
                .mapToObj(value -> tournamentSelection(tSize))
                .collect(Collectors.toList());

    }

    private Chromosome tournamentSelection(int tSize) {
        List<Chromosome> tournament = new ArrayList<>(chromosomes);
        Collections.shuffle(tournament);
        tournament = tournament.subList(0, tSize);
        Optional<Chromosome> chromosome = tournament.stream().min(Comparator.comparing(Chromosome::evaluate));
        return chromosome.orElse(chromosomes.get(0));
    }

    private void applyOperators() {
        
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
