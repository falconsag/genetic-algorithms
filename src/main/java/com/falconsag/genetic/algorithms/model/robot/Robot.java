package com.falconsag.genetic.algorithms.model.robot;

import static com.falconsag.genetic.algorithms.model.robot.Constants.*;
import static com.falconsag.genetic.algorithms.robot.Evaluator.SIGHT_DISTANCE;

public class Robot {

    private int hp;
    private Coord coord;
    private Coord dir;

    @Override
    public Robot clone() {
        return new Robot(this.hp, coord.clone(), dir.clone());
    }

    public Robot(int hp, Coord coord, Coord dir) {
        this.hp = hp;
        this.coord = coord;
        this.dir = dir;
    }

    public boolean isOutOfEnergy() {
        return hp <= 0;
    }

    public int getHp() {
        return hp;
    }

    public Coord getCoord() {
        return coord;
    }

    public Coord getDir() {
        return dir;
    }

    public void addHp(int d) {
        this.hp += d;
//        if (hp >= ROBOT_MAX_HP) {
//            hp = ROBOT_MAX_HP;
//        }
    }

    public int getSensorBitmap(Coord foodCord) {
        Coord dist = Coord.distance(coord, foodCord);
        int x = dist.getX();
        int y = dist.getY();
        if (Math.abs(x) <= SIGHT_DISTANCE && Math.abs(y) <= SIGHT_DISTANCE) {
            int paraD = x;
            int perpeD = y;
            if (dir.equals(DIR_RIGHT)) {
                paraD = x;
                perpeD = y;
            } else if (dir.equals(DIR_DOWN)) {
                paraD = y;
                perpeD = -x;
            } else if (dir.equals(DIR_LEFT)) {
                paraD = -x;
                perpeD = -y;
            } else if (dir.equals(DIR_UP)) {
                paraD = -y;
                perpeD = x;
            }

            boolean sees = paraD >= 0;
            if (sees) {
                return (encodeOnThreeBits(paraD) << 3) | encodeOnThreeBits(perpeD);
            } else {
                return 0;
            }
        }
        return 0;
    }

    public static int encodeOnThreeBits(int val) {
        if (val < 0) {
            val = ((Math.abs(val) ^ 0b11) + 1) | 0b100;
        }
        return val;
    }

    public void takeAction(Action a, int hpCost) {
        switch (a) {
            case FORWARD:
                coord = Coord.add(coord, dir);
                break;
            case TURN_LEFT:
                dir = getTurnLeft();
                break;
            case TURN_RIGHT:
                dir = getTurnRight();
                break;
        }
        hp -= hpCost;
    }

    public static int percentBitmap(int val, int maxVal) {
        double v = (double) val / maxVal;
        if (v < 0.05) {
            return _0_5_PERCENT;
        } else if (v < 0.15) {
            return _5_15_PERCENT;
        } else if (v < 0.60) {
            return _15_60_PERCENT;
        } else {
            return _60_100_PERCENT;
        }
    }

    private Coord getTurnLeft() {
        if (dir.equals(DIR_LEFT)) {
            return DIR_DOWN;
        }
        if (dir.equals(DIR_DOWN)) {
            return DIR_RIGHT;
        }
        if (dir.equals(DIR_RIGHT)) {
            return DIR_UP;
        }
        if (dir.equals(DIR_UP)) {
            return DIR_LEFT;
        }
        return null;
    }

    private Coord getTurnRight() {
        if (dir.equals(DIR_LEFT)) {
            return DIR_UP;
        }
        if (dir.equals(DIR_UP)) {
            return DIR_RIGHT;
        }
        if (dir.equals(DIR_RIGHT)) {
            return DIR_DOWN;
        }
        if (dir.equals(DIR_DOWN)) {
            return DIR_RIGHT;
        }
        return null;
    }
}
