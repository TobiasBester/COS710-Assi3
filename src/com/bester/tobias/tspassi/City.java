package com.bester.tobias.tspassi;

import javafx.util.Pair;

public class City {

    private int index;

    private Double x;

    private Double y;
    private Pair<Double, Double> coords;
    public City(int index, Double x, Double y) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.coords = new Pair<>(x, y);
    }

    City(String index, String x, String y) {
        this.index = Integer.valueOf(index);
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
        this.coords = new Pair<>(this.x, this.y);
    }

    @Override
    public String toString() {
        return String.format("%n- City %d: (%f, %f)", index, x, y);
    }

    Double getX() {
        return x;
    }

    Double getY() {
        return y;
    }
}
