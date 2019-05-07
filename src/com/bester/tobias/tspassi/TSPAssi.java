package com.bester.tobias.tspassi;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

public class TSPAssi extends Application {

    private static String problemName = "st70";
    private static int numChromosomes = 20;
    private static int numIterations = 10000;
    private static int tournamentSize = 8;
    private double mutationProbability = 0.05;
    private Boolean directRepresentation = false;

    public static void main(String[] args) {
        ArrayList<Optional<String>> optArgs = new ArrayList<>();
        IntStream.range(0, args.length).forEach(i -> optArgs.add(Optional.of(args[i])));
        IntStream.range(args.length, 4).forEach(i -> optArgs.add(Optional.empty()));

        problemName = parseProblemArg(Integer.parseInt(optArgs.get(0).orElse("10")));
        numChromosomes = Integer.parseInt(optArgs.get(1).orElse("20"));
        numIterations = Integer.parseInt(optArgs.get(2).orElse("10000"));
        tournamentSize = Integer.parseInt(optArgs.get(3).orElse("4"));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.format("Starting TSP Project for problem %s%n", problemName);

        if (directRepresentation) {
            directRepresentation();
        } else {
            indirectRepresentation();
        }

    }

    private void directRepresentation() {
        DirectPopulation directPopulation = new DirectPopulation(problemName, numChromosomes, numIterations);
        directPopulation.createInitialPopulation();
        directPopulation.startSearch(tournamentSize, mutationProbability);
        directPopulation.printHistory();
//        directPopulation.printAverageFitnessGraph();
//        directPopulation.printBestFitnessGraph();
    }

    private void indirectRepresentation() {
        IndirectPopulation indirectPopulation = new IndirectPopulation(problemName, numChromosomes, numIterations);
        indirectPopulation.createInitialPopulation();
        indirectPopulation.startSearch(tournamentSize, mutationProbability);
        indirectPopulation.printHistory();
        indirectPopulation.printAverageFitnessGraph();
        indirectPopulation.printBestFitnessGraph();
    }

    private static String parseProblemArg(int number) {
        switch (number) {
            case 0: return "a280";
            case 1: return "berlin52";
            case 2: return "bier127";
            case 3: return "brd14051";
            case 4: return "ch130";
            case 5: return "lin318";
            case 6: return "pr124";
            case 7: return "pr1002";
            case 8: return "rat195";
            case 9: return "rat783";
            default: return "st70";
        }
    }
}
