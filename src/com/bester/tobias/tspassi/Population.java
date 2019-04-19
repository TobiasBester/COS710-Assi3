package com.bester.tobias.tspassi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Population {

    private String problemName;
    private String comment;
    private int dimension;
    private List<String> coords;
    private List<City> cities;

    Population(String problemName, boolean directRepresentation) {
        this.problemName = problemName;
        readDataFromFile(problemName);
        saveCities();
    }

    void startSearch() {
        System.out.println("Starting search");
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
