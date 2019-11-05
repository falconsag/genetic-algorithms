package com.falconsag.genetic.algorithms.robot;

import com.falconsag.genetic.algorithms.model.Chromosome;
import com.falconsag.genetic.algorithms.model.robot.Action;
import static com.falconsag.genetic.algorithms.model.robot.Constants.*;
import com.falconsag.genetic.algorithms.model.robot.Coord;
import com.falconsag.genetic.algorithms.model.robot.Food;
import com.falconsag.genetic.algorithms.model.robot.GameState;
import com.falconsag.genetic.algorithms.model.robot.RandomInterval;
import com.falconsag.genetic.algorithms.model.robot.Robot;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

public class Evaluator {

    private Chromosome algo;

    public static int FOOD_MIN_VAL = 1500;
    public static int FOOD_MAX_VAL = 2000;
    public static int FODD_VALUE_DECREASE = 100;
    public static int TURN_COST = 10;
    public static int MOVE_COST = 20;
    public static int DO_NOTHING_COST = 0;
    public static int NUMBER_OF_SIMULATE_STEPS = 10000;
    public static int MAZE_SIZE = 15;

    public static int ROBOT_MAX_HP = 1000;
    public static final int SIGHT_DISTANCE = 3;
    private static final Random rand = new Random();
    //BITMAP
    // distX  distY  ahead  rHP  fHP
    //  000    000    00    00   00
    private static final int BITMASK_SEES = 0b111111000000;
    private static final int BITMASK_AHEAD = 0b000000110000;
    private static final int BITMASK_RHP = 0b000000001100;
    private static final int BITMASK_FHP = 0b000000000011;

    private RandomInterval randomFieldPosition = new RandomInterval(1, MAZE_SIZE - 2);
    private RandomInterval randomFoodValue = new RandomInterval(FOOD_MIN_VAL, FOOD_MAX_VAL);
    private Coord INITIAL_ROBOT_POS = new Coord(1, 3);
    private Coord INITIAL_ROBOT_DIR = DIR_RIGHT;
    public static int[] field;

    public Evaluator(Chromosome algo) {
        this.algo = algo;
        field = new int[MAZE_SIZE * MAZE_SIZE];
        for (int i = 0; i < MAZE_SIZE; i++) {
            field[getIndex(i, 0)] = WALL_FIELD;
            field[getIndex(0, i)] = WALL_FIELD;
            field[getIndex(i, MAZE_SIZE - 1)] = WALL_FIELD;
            field[getIndex(MAZE_SIZE - 1, i)] = WALL_FIELD;
        }
    }


    public double evaluate(List<GameState> gameStates) {
        Deque<Food> foodStack = new ArrayDeque<>();

        Robot robot = new Robot(ROBOT_MAX_HP, INITIAL_ROBOT_POS, INITIAL_ROBOT_DIR);
        Food food = getRandomFood();
        int i = 0;
        long accumHP = ROBOT_MAX_HP;
        food = getFood(foodStack);
        int numFoodPickedUp = 0;
        double sumEnergyUsed = 0;
        while (i < NUMBER_OF_SIMULATE_STEPS) {
            if (field[getIndex(robot.getCoord())] == WALL_FIELD || robot.isOutOfEnergy()) {
                appendGameState(gameStates, robot, food, null);
                return 0;
            }

            appendGameState(gameStates, robot, food, getSensorIndexBitmap(robot, food));
            int sensorIndexBitmap = getSensorIndexBitmap(robot, food);
//            String stringRepresentation = getStringRepresentation(sensorIndexBitmap);
//            System.out.println(stringRepresentation);
            Action action = getRobotAction(sensorIndexBitmap);
            int costOfAction = getCostOfAction(action);
            sumEnergyUsed += costOfAction;
            robot.takeAction(action, costOfAction);
            //apply hp
            if (robot.getCoord().equals(food.getCoord())) {
                robot.addHp(food.consume());
                numFoodPickedUp++;
            }
            if (food.isEmpty()) {
                food = getFood(foodStack);
            }
            food.decreaseValue(FODD_VALUE_DECREASE);

            accumHP += robot.getHp();
            i++;
        }
        appendGameState(gameStates, robot, food, null);
        return numFoodPickedUp;
    }


    private int getCostOfAction(Action a) {
        switch (a) {
            case NOTHING:
                return DO_NOTHING_COST;
            case FORWARD:
                return MOVE_COST;
            default:
                return TURN_COST;
        }
    }

    private Food getFood(Deque<Food> foodStack) {
        Food food = foodStack.pollFirst();
        if (food == null) {
            food = getRandomFood();
        }
        return food;
    }

    private String getStringRepresentation(int i) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int j = 0; j < 12; j++) {
            int mask = 1 << j;
            int val = (i & mask) >> j;

            stack.push(val);
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Integer h : stack) {
            if (index == 3 || index == 6 || index == 8 || index == 10) {
                sb.append(" ");
            }
            index++;
            sb.append("" + h);
        }
        return sb.toString();
    }

    private boolean isObjectAhead(int sensorIndexBitmap, int objectCode) {
        return ((sensorIndexBitmap & BITMASK_AHEAD) >> 4) == objectCode;
    }

    private void appendGameState(List<GameState> gameStates, Robot robot, Food food, Integer sensorIndexBitmap) {
        GameState gs = new GameState();
        gs.setRobot(robot.clone());
        gs.setSingleFood(food.clone());
        gs.setSeesFood(sensorIndexBitmap != null && ((sensorIndexBitmap & BITMASK_SEES) != 0));
        if (sensorIndexBitmap != null) {
            gs.setAhead((sensorIndexBitmap & BITMASK_AHEAD) >> 4);
            gs.setrHP((sensorIndexBitmap & BITMASK_RHP) >> 2);
            gs.setfHP((sensorIndexBitmap & BITMASK_FHP));
        }
        gameStates.add(gs);
    }

    private boolean isSeesFood(int sensorIndexBitmap) {
        return ((sensorIndexBitmap & BITMASK_SEES) != 0);
    }

    private int getSensorIndexBitmap(Robot robot, Food food) {
        int sensorBitmap = robot.getSensorBitmap(food.getCoord());
        Coord coordAhead = Coord.add(robot.getCoord(), robot.getDir());
        int aheadBitmap = getAheadBitmap(field[getIndex(coordAhead)]);
        int rHPbitmap = Robot.percentBitmap(robot.getHp(), ROBOT_MAX_HP);
        int fHPbitmap = getfoodValBitmap(food, sensorBitmap);
        int sensorIndexBitmap = ((sensorBitmap << 2 | aheadBitmap) << 2 | rHPbitmap) << 2 | fHPbitmap;
        return sensorIndexBitmap;
    }

    private Action getRobotAction(int sensorIndexBitmap) {
        List<Integer> genes = algo.getGenes();
        int actionOrdinal = genes.get(sensorIndexBitmap) * 2 + genes.get(sensorIndexBitmap + 1);
        Action action = Action.values()[actionOrdinal];

        boolean nothingAhead = isObjectAhead(sensorIndexBitmap, 0);
        boolean wallAhead = isObjectAhead(sensorIndexBitmap, WALL_CODING);

        if (isSeesFood(sensorIndexBitmap)) {
            return action;
        } else if (nothingAhead) {
            //give some % to just move
            double movePct = 0.9;
            if (rand.nextDouble() < movePct) {
                return Action.FORWARD;
            } else {
                return getRandomAction(Action.values());
            }
        } else if (wallAhead) {
            return getRandomAction(new Action[]{Action.NOTHING, Action.TURN_LEFT, Action.TURN_RIGHT});
        }

        return action;
    }

    private Action getRandomAction(Action[] actionValues) {
        return actionValues[rand.nextInt(actionValues.length)];
    }

    private int getfoodValBitmap(Food food, int sensorBitmap) {
        int fHPbitmap = 0;
        if (sensorBitmap != 0) {
            return fHPbitmap = Robot.percentBitmap(food.getValue(), ROBOT_MAX_HP);
        }
        return 0;
    }

    private int getAheadBitmap(int fieldValue) {
        int ahead = 0;
        if (fieldValue == WALL_FIELD) {
            ahead = WALL_CODING;
        } else if (fieldValue == WATER_FIELD) {
            ahead = WATER_CODING;
        }
        return ahead;
    }

    private Food getRandomFood() {
        Coord coord = new Coord(randomFieldPosition.getRandom(), randomFieldPosition.getRandom());
        return new Food(coord, randomFoodValue.getRandom());
    }

    public static int[] getField() {
        return field;
    }

    private static int getIndex(int x, int y) {
        return y * MAZE_SIZE + x;
    }

    private static int getIndex(Coord coord) {
        return getIndex(coord.getX(), coord.getY());
    }

    public static void setDoNothingCost(int doNothingCost) {
        DO_NOTHING_COST = doNothingCost;
    }

    public static void setTurnCost(int turnCost) {
        TURN_COST = turnCost;
    }

    public static void setMoveCost(int moveCost) {
        MOVE_COST = moveCost;
    }

    public static void setNumberOfSimulationSteps(int numberOfSimulationSteps) {
        NUMBER_OF_SIMULATE_STEPS = numberOfSimulationSteps;
    }

    public static void setFoodMin(int foodMin) {
        FOOD_MIN_VAL = foodMin;
    }

    public static void setFoodMax(int foodMax) {
        FOOD_MAX_VAL = foodMax;
    }

    public static void setFoodDecrease(int foodDecrease) {
        FODD_VALUE_DECREASE = foodDecrease;
    }


    public static void setMazeSize(int mazeSize) {
        MAZE_SIZE = mazeSize;
    }

}
