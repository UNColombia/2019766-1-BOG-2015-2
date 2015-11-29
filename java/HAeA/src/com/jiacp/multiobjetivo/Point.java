package com.jiacp.multiobjetivo;

import java.util.List;

/**
 * Created by jiacontrerasp on 11/18/15.
 */
public class Point {
    private int id;
    private Double x;
    private Double y;
    private int fitness;
    private List<Integer> dominate;
    private List<Integer> dominated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public List<Integer> getDominate() {
        return dominate;
    }

    public void setDominate(List<Integer> dominate) {
        this.dominate = dominate;
    }

    public List<Integer> getDominated() {
        return dominated;
    }

    public void setDominated(List<Integer> dominated) {
        this.dominated = dominated;
    }
}
