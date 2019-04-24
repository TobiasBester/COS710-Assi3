package com.bester.tobias.tspassi;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

    private List<Number> averageFitnessHistory = new ArrayList<>();
    private List<Number> bestFitnessHistory = new ArrayList<>();

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
        IntStream.range(0, numChromosomes).forEach(cIdx -> chromosomes.add(new Chromosome(cIdx, cities, true)));
    }

    void startSearch(int tournamentSize, double mutationProbability) {
        System.out.println("Starting search");
        IntStream.range(0, numIterations).forEach(idx -> {
            System.out.format("Starting iteration %d%n", idx);
            evaluatePopulation();

            List<Chromosome> parents = selectParents(tournamentSize);

            chromosomes = new ArrayList<>(applyOperators(parents, mutationProbability));
            averageFitnessHistory.add(calculateAverageFitness());
            bestFitnessHistory.add(calculateBestFitness());
        });
    }

    void printHistory() {
        System.out.println("Average Fitness: " + averageFitnessHistory);
        System.out.println("Best fitness: " + bestFitnessHistory);
    }

    void printAverageFitnessGraph() {
        printGraph(averageFitnessHistory, "Average Fitness");
    }

    void printBestFitnessGraph() {
        printGraph(bestFitnessHistory, "Best Fitness");
    }

    private void printGraph(List<Number> history, String label) {
        Stage stage = new Stage();
        stage.setTitle("Fitness over time");
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Iterations");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(label);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series series = new XYChart.Series();
        series.setName("Fitness over time");

        IntStream.range(0, numIterations).forEach(idx ->
                series.getData().add(new Data(idx + 1, history.get(idx))));

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    private Integer calculateBestFitness() {
        Optional<Integer> lowest = chromosomes.stream()
                .map(Chromosome::getCurrentFitness)
                .min(Integer::compareTo);

        return lowest.orElse(9999);
    }

    private float calculateAverageFitness() {
        Optional<Integer> sum = chromosomes.stream()
                .map(Chromosome::getCurrentFitness)
                .reduce(Integer::sum);

        return sum.orElse(0) / ((float) chromosomes.size());
    }

    private void evaluatePopulation() {
        IntStream.range(0, chromosomes.size()).forEach(cIdx -> {
//            System.out.format(
//                    "Evaluating Chromosome %d: %d%n",
//                    chromosomes.get(cIdx).getId(),
//                    chromosomes.get(cIdx).evaluate());
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
        float r;
        int idCounter = 0;

        while (!chromosomes.isEmpty()) {
            r = new Random().nextFloat();

            if (chromosomes.size() > 1 && r > mutationProb) {
                List<Chromosome> selected = getTwoRandomChromosomesFromList(chromosomes);
                newGen.addAll(crossover(selected.get(0), idCounter++, selected.get(1), idCounter++));
            } else {
                Chromosome selected = getRandomChromosomeFromList(chromosomes);
                newGen.add(mutation(selected, idCounter++));
            }
        }

        assert newGen.size() == targetSize;

        return newGen;
    }

    private Chromosome mutation(Chromosome chromosome, int idCounter) {
//        System.out.format("Performing mutation on %d%n", chromosome.getId());

        int[] indexes =  chromosome.getTwoRandomIndexes();
        int idx1 = indexes[0];
        int idx2 = indexes[1];

        List<City> newCities = new ArrayList<>(chromosome.getCities());
        Collections.swap(newCities, idx1, idx2);
        return new Chromosome(idCounter, newCities, false);
    }

    private List<Chromosome> crossover(Chromosome chromosome1, int id1, Chromosome chromosome2, int id2) {
//        System.out.format("Performing crossover between %d and %d%n",
//                chromosome1.getId(),
//                chromosome2.getId());

        int[] indexes =  chromosome1.getTwoRandomIndexes();
        int idx1 = indexes[0];
        int idx2 = indexes[1];
        Chromosome child1 = new Chromosome(chromosome1);
        Chromosome child2 = new Chromosome(chromosome2);

        List<City> parent1Cities = chromosome1.getCities();
        List<City> parent2Cities = chromosome2.getCities();
        List<City> child1Cities = getEmptyListOfCities(child1.getCities().size());
        List<City> child2Cities = getEmptyListOfCities(child2.getCities().size());

        List<Integer> frozenCities1 = parent1Cities.subList(idx1, idx2).stream()
                .map(City::getId)
                .collect(Collectors.toList());
        List<Integer> frozenCities2 = parent2Cities.subList(idx1, idx2).stream()
                .map(City::getId)
                .collect(Collectors.toList());

        IntStream.range(idx1, idx2 + 1).forEach(idx -> {
            child1Cities.set(idx, parent1Cities.get(idx));
            child2Cities.set(idx, parent2Cities.get(idx));
        });

        fillChildCities(child1Cities, frozenCities1, parent2Cities, idx1, idx2);
        fillChildCities(child2Cities, frozenCities2, parent1Cities, idx1, idx2);

        List<Chromosome> results = new ArrayList<>();
        results.add(new Chromosome(id1, child1Cities, false));
        results.add(new Chromosome(id2, child2Cities, false));
        return results;
    }

    private void fillChildCities(
            List<City> childCities,
            List<Integer> frozenCities,
            List<City> parentCities,
            int idx1,
            int idx2
    ) {
        AtomicInteger rangeCounter = new AtomicInteger(0);

        IntStream.range(0, parentCities.size()).forEach(idx -> {
            int cityId = parentCities.get(idx).getId();

            if (!indexIsOutsideOfRange(rangeCounter.get(), idx1, idx2)) {
                rangeCounter.set(idx2);
            }

            if (!frozenCities.contains(cityId)) {
                childCities.set(rangeCounter.getAndIncrement(), parentCities.get(idx));
            }

        });

        if (!checkForDuplicates(childCities)) {
            System.out.println("AAAAAAAAAAAAAAAAAH\n\n\n\n\n");
        }

    }

    private boolean checkForDuplicates(List<City> childCities) {
        Set<Integer> set = new HashSet<>();
        AtomicBoolean result = new AtomicBoolean(true);
        childCities.stream()
                .map(City::getId)
                .forEach(integer -> {
                    if (!set.add(integer)) {
                        result.set(false);
                    }
                });

        return result.get();
    }

    private boolean indexIsOutsideOfRange(int index, int bound1, int bound2) {
        return (index < bound1 || index > bound2);
    }

    private List<City> getEmptyListOfCities(int size) {
        return new ArrayList<>(Collections.nCopies(size, null));
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
