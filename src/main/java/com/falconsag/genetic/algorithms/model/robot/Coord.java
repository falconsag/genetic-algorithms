package com.falconsag.genetic.algorithms.model.robot;

import com.google.common.base.Objects;

public class Coord {
    private int x;
    private int y;


    @Override
    protected Coord clone() {
        return new Coord(x, y);
    }

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public static Coord of(int x, int y) {
        return new Coord(x, y);
    }

    public static Coord distance(Coord c1, Coord c2) {
        return new Coord(c2.x - c1.x, c2.y - c1.y);
    }

    public static Coord add(Coord c1, Coord c2) {
        return new Coord(c1.x + c2.x, c1.y + c2.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return x == coord.x &&
                y == coord.y;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }

    public static Coord multiply(Coord c, int val) {
        return new Coord(c.x * val, c.y * val);
    }
}
