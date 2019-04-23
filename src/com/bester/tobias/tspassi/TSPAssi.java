package com.bester.tobias.tspassi;

public class TSPAssi {

    public static void main(String[] args) {
        System.out.println("Starting TSP Project");

        String problemName = "st70";
        boolean directRepresentation = true;
        int numChromosomes = 10;
        int numIterations = 1;
        int tournamentSize = 4;
        double mutationProbability = 0.20;

        Population population = new Population(problemName, numChromosomes, numIterations, directRepresentation);
        population.createInitialPopulation();
        population.startSearch(tournamentSize, mutationProbability);
    }

}
