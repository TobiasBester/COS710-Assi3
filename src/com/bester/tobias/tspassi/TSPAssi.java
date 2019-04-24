package com.bester.tobias.tspassi;

import javafx.application.Application;
import javafx.stage.Stage;

public class TSPAssi extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting TSP Project");

        String problemName = "st70";
        boolean directRepresentation = true;
        int numChromosomes = 20;
        int numIterations = 10000;
        int tournamentSize = 4;
        double mutationProbability = 0.70;

        Population population = new Population(problemName, numChromosomes, numIterations, directRepresentation);
        population.createInitialPopulation();
        population.startSearch(tournamentSize, mutationProbability);
        population.printHistory();
        population.printAverageFitnessGraph();
        population.printBestFitnessGraph();
    }
}
