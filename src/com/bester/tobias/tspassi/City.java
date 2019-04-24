package com.bester.tobias.tspassi;

import javafx.util.Pair;

public class City {

    private int id;

    private Double x;

    private Double y;

    private Pair<Double, Double> coords;

    public City(int id, Double x, Double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.coords = new Pair<>(x, y);
    }

    City(String id, String x, String y) {
        this.id = Integer.valueOf(id);
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
        this.coords = new Pair<>(this.x, this.y);
    }
    @Override
    public String toString() {
        return String.format("%d", id);
    }

    public int getId() {
        return id;
    }

    Double getX() {
        return x;
    }

    Double getY() {
        return y;
    }

    Pair<Double, Double> getCoords() {
        return coords;
    }
}
