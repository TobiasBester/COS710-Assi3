package com.bester.tobias.tspassi;

public class TSPAssi {

    public static void main(String[] args) {
        System.out.println("Starting TSP Project");

        String problemName = "ch130";
        boolean directRepresentation = true;

        Population population = new Population(problemName, directRepresentation);
        System.out.println(population);
        population.startSearch();
    }

}
