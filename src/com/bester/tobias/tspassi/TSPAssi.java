package com.bester.tobias.tspassi;

public class TSPAssi {

    public static void main(String[] args) {
        System.out.println("Starting TSP Project");

        String problemName = "st70";
        boolean directRepresentation = true;

        Population population = new Population(problemName, 8, 1, directRepresentation);
        population.createInitialPopulation();
        population.startSearch();
    }

}
