package com.falconsag.genetic.algorithms.model.robot;

public class Food {
    private Coord pos;
    private int value;

    @Override
    public Food clone() {
        return new Food(pos.clone(), value);
    }

    public Food(Coord pos, int value) {
        this.pos = pos;
        this.value = value;
    }

    public void decreaseValue(int d) {
        value -= d;
    }

    public boolean isEmpty() {
        return value <= 0;
    }

    public int consume() {
        int val = value;
        value = 0;
        return val;
    }

    public Coord getCoord() {
        return pos;
    }

    public int getValue() {
        return value;
    }

    public void setPos(Coord pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "Food{" +
                "pos=" + pos +
                ", value=" + value +
                '}';
    }
}
