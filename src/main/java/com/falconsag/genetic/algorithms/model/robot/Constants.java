package com.falconsag.genetic.algorithms.model.robot;

public class Constants {
    public static final int WALL_FIELD = -1;
    public static final int WATER_FIELD = -2;


    public static final int WALL_CODING = 1;
    public static final int WATER_CODING = 2;

    public static final Coord DIR_RIGHT = new Coord(1, 0);
    public static final Coord DIR_LEFT = new Coord(-1, 0);
    public static final Coord DIR_UP = new Coord(0, -1);
    public static final Coord DIR_DOWN = new Coord(0, 1);

    public static final int _0_5_PERCENT = 0;
    public static final int _5_15_PERCENT = 1;
    public static final int _15_60_PERCENT = 2;
    public static final int _60_100_PERCENT = 3;
}
