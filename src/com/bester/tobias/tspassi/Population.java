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

    void startSearch(int tournamentSize, double mutationProbability) {
        System.out.println("Starting search");
        IntStream.range(0, numIterations).forEach(idx -> {
            System.out.println(String.format("Starting iteration %d", idx));
            evaluatePopulation();
            List<Chromosome> parents = selectParents(tournamentSize);
            System.out.println("Parents");
            System.out.println(parents);
            chromosomes = applyOperators(parents, mutationProbability);
        });
    }

    private void evaluatePopulation() {
        IntStream.range(0, chromosomes.size()).forEach(cIdx -> {
            System.out.println(String.format(
                    "Evaluating Chromosome %d: %d",
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

    private List<Chromosome> applyOperators(List<Chromosome> chromosomes, double mutationProb) {
        int targetSize = chromosomes.size();
        List<Chromosome> newGen = new ArrayList<>();
        float r = new Random().nextFloat();

        while (!chromosomes.isEmpty()) {
            if (chromosomes.size() > 1 && r > mutationProb) {
                List<Chromosome> selected = getTwoRandomChromosomesFromList(chromosomes);
                newGen.addAll(crossover(selected.get(0), selected.get(1)));
            } else {
                Chromosome selected = getRandomChromosomeFromList(chromosomes);
                newGen.add(mutation(selected));
            }
        }

        assert newGen.size() == targetSize;

        return newGen;
    }

    private Chromosome mutation(Chromosome chromosome) {
        int[] indexes =  chromosome.getTwoRandomIndexes();
        int idx1 = indexes[0];
        int idx2 = indexes[1];
        Chromosome result = new Chromosome(chromosome);
        Collections.swap(result.getCities(), idx1, idx2);
        return result;
    }

    private List<Chromosome> crossover(Chromosome chromosome1, Chromosome chromosome2) {
        int[] indexes =  chromosome1.getTwoRandomIndexes();
        int idx1 = indexes[0];
        int idx2 = indexes[1];
        Chromosome result1 = new Chromosome(chromosome1);
        Chromosome result2 = new Chromosome(chromosome2);

        // Swaperoni
        result1.getCities().set(idx1, chromosome1.getCities().get(idx2));
        result2.getCities().set(idx2, chromosome2.getCities().get(idx1));

        List<Chromosome> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);
        return results;
    }

    private Chromosome getRandomChromosomeFromList(List<Chromosome> list) {
        Chromosome result = list.get(new Random().nextInt(list.size()));
        list.remove(result);
        return result;
    }

    private List<Chromosome> getTwoRandomChromosomesFromList(List<Chromosome> list) {
        List<Chromosome> result = new ArrayList<>();
        Chromosome chromosome1 = getRandomChromosomeFromList(list);
        Chromosome chromosome2 = getRandomChromosomeFromList(list);

        result.add(chromosome1);
        result.add(chromosome2);
        return result;
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
