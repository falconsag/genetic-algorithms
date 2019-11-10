package com.falconsag.genetic.algorithms.robot;

import com.falconsag.genetic.algorithms.model.EditorConfiguration;
import com.falconsag.genetic.algorithms.model.GeneticConfiguration;
import com.falconsag.genetic.algorithms.model.SimulatorConfiguration;
import com.falconsag.genetic.algorithms.model.robot.Action;
import static com.falconsag.genetic.algorithms.model.robot.Constants.*;
import com.falconsag.genetic.algorithms.model.robot.Coord;
import com.falconsag.genetic.algorithms.model.robot.Food;
import com.falconsag.genetic.algorithms.model.robot.GameSimulation;
import com.falconsag.genetic.algorithms.model.robot.GameState;
import com.falconsag.genetic.algorithms.model.robot.RandomInterval;
import com.falconsag.genetic.algorithms.model.robot.Robot;
import io.jenetics.BitChromosome;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Evaluator {

    private BitChromosome chromosome;
    private SimulatorConfiguration simulatorConfig;
    private EditorConfiguration editorConfig;
    private GeneticConfiguration config;
    public int foodMinVal = 1500;
    public int foodMaxVal = 2000;
    public int foodValueDecrease = 100;
    public int turnCost = 10;
    public int moveCost = 20;
    public int doNothingCost = 0;
    public int numberOfSimulateSteps = 10000;
    public int mazeSize;

    public static int ROBOT_MAX_HP = 1000;
    public static final int SIGHT_DISTANCE = 3;
    private final Random rand = new Random();
    //BITMAP
    // distX  distY  ahead  rHP  fHP
    //  000    000    00    00   00
    private static final int BITMASK_SEES = 0b111111000000;
    private static final int BITMASK_AHEAD = 0b000000110000;
    private static final int BITMASK_RHP = 0b000000001100;
    private static final int BITMASK_FHP = 0b000000000011;
    public final double MOVE_SCORE = 50;
    private List<Integer> freeIndexes = new ArrayList<>();
    private RandomInterval randomFieldPositionGenerator;
    private RandomInterval randomFoodValueGenerator;
    private Coord initialRobotPos;
    private Coord initialRobotDir;
    public List<Integer> fields;
    public Deque<Food> FOOD_STACK;
    public Deque<Food> foodStack;
    private ConcurrentLinkedDeque<GameSimulation> simulationsDeque;

    public static int counter = 0;

    public Evaluator(GeneticConfiguration config, BitChromosome chromosome) {
        counter++;
        this.chromosome = chromosome;
        this.config = config;
        this.editorConfig = config.getEditorConfig();
        this.simulatorConfig = config.getSimulatorConfig();

        fields = config.getEditorConfig().getMapArr();
        initialRobotPos = new Coord(editorConfig.getRobot().getX(), editorConfig.getRobot().getY());
        initialRobotDir = new Coord(editorConfig.getRobot().getDirX(), editorConfig.getRobot().getDirY());
        mazeSize = editorConfig.getMazeSize();
        freeIndexes = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i) == 0) {
                freeIndexes.add(i);
            }
        }
        randomFieldPositionGenerator = new RandomInterval(0, freeIndexes.size() - 1);
        foodMaxVal = simulatorConfig.getFoodMax();
        foodMinVal = simulatorConfig.getFoodMin();
        randomFoodValueGenerator = new RandomInterval(foodMinVal, foodMaxVal);
        foodValueDecrease = simulatorConfig.getFoodDecrease();
        turnCost = simulatorConfig.getTurnCost();
        moveCost = simulatorConfig.getMoveCost();
        doNothingCost = simulatorConfig.getDoNothingCost();
        numberOfSimulateSteps = simulatorConfig.getNumberOfSimulateSteps();

        //legacy to preprogram some initial food positions
        FOOD_STACK = new ArrayDeque<>();
//        for (int i = 0; i < 0; i++) {
//            Coord coord = new Coord(RANDOM_FIELD_POSITION.getRandom(), RANDOM_FIELD_POSITION.getRandom());
//            FOOD_STACK.add(new Food(coord, RANDOM_FOOD_VALUE.getRandom()));
//        }
        foodStack = new ArrayDeque<>();
        for (Food food : FOOD_STACK) {
            foodStack.add(food.clone());
        }
    }


    public double evaluate(ConcurrentLinkedDeque<GameSimulation> simulations) {
        this.simulationsDeque = simulations;
        List<GameState> gameStates = new ArrayList<GameState>();
        Robot robot = new Robot(ROBOT_MAX_HP, initialRobotPos, initialRobotDir);
        int i = 0;
        long accumHP = ROBOT_MAX_HP;
        Food food = getFood(foodStack);
        int fitness = 0;
        double sumEnergyUsed = 0;
        int foodConsumeCount = 0;
        while (i < numberOfSimulateSteps) {
            if (fields.get(getIndex(robot.getCoord())) == WALL_FIELD || robot.isOutOfEnergy()) {
                appendGameState(gameStates, robot, food, null);
                if (robot.isOutOfEnergy()) {
                    fitness = fitness / 2;
                } else {
                    fitness = 0;
                }
                simulationsDeque.add(new GameSimulation(gameStates, fitness));
                return fitness;
            }
            appendGameState(gameStates, robot, food, getSensorIndexBitmap(robot, food));
            int sensorIndexBitmap = getSensorIndexBitmap(robot, food);
//            String stringRepresentation = getStringRepresentation(sensorIndexBitmap);
//            System.out.println(stringRepresentation);
            Action action = getRobotAction(sensorIndexBitmap);
            int costOfAction = getCostOfAction(action);
            sumEnergyUsed += costOfAction;
            robot.takeAction(action, costOfAction);
            if (Action.FORWARD.equals(action)) {
                fitness += MOVE_SCORE;
            }
            //apply hp
            if (robot.getCoord().equals(food.getCoord())) {
                int foodVal = food.consume();
                robot.addHp(foodVal);
                foodConsumeCount++;
                fitness += (foodVal * foodConsumeCount);
            }
            if (food.isEmpty()) {
                food = getFood(foodStack);
            }
            food.decreaseValue(foodValueDecrease);

            accumHP += robot.getHp();
            i++;
        }
        appendGameState(gameStates, robot, food, null);
        simulationsDeque.add(new GameSimulation(gameStates, fitness));
        return fitness;
    }


    private int getCostOfAction(Action a) {
        switch (a) {
            case NOTHING:
                return doNothingCost;
            case FORWARD:
                return moveCost;
            default:
                return turnCost;
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
        int aheadBitmap = getAheadBitmap(fields.get(getIndex(coordAhead)));
        int rHPbitmap = Robot.percentBitmap(robot.getHp(), ROBOT_MAX_HP);
        int fHPbitmap = getfoodValBitmap(food, sensorBitmap);
        int sensorIndexBitmap = ((sensorBitmap << 2 | aheadBitmap) << 2 | rHPbitmap) << 2 | fHPbitmap;
        return sensorIndexBitmap;
    }

    private Action getRobotAction(int sensorIndexBitmap) {

        int firstBit = chromosome.getGene(sensorIndexBitmap).getAllele() ? 1 : 0;
        int secondBit = chromosome.getGene(sensorIndexBitmap + 1).getAllele() ? 1 : 0;

        int actionOrdinal = firstBit * 2 + secondBit;
        Action action = Action.values()[actionOrdinal];


//        int lowerBitValue = sensorIndexBitmap & 3;
//        if (lowerBitValue == 0) {
//            return getRandomAction(Action.values());
//        } else if (lowerBitValue == 1) {
//            double movePct = 0.5;
//            if (rand.nextDouble() < movePct) {
//                return Action.FORWARD;
//            } else {
//                return getRandomAction(new Action[]{Action.NOTHING, Action.TURN_LEFT, Action.TURN_RIGHT});
//            }
//        } else if (lowerBitValue == 2) {
//            double movePct = 0.9;
//            if (rand.nextDouble() < movePct) {
//                return Action.FORWARD;
//            } else {
//                return getRandomAction(new Action[]{Action.NOTHING, Action.TURN_LEFT, Action.TURN_RIGHT});
//            }
//        } else {
//            return action;
//        }

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
        Integer freeI = freeIndexes.get(randomFieldPositionGenerator.getRandom());
        Coord coord = iToXY(freeI);
        return new Food(coord, randomFoodValueGenerator.getRandom());
    }

    public List<Integer> getFields() {
        return fields;
    }

    private int getIndex(int x, int y) {
        return y * mazeSize + x;
    }

    private Coord iToXY(int i) {
        return new Coord(i % mazeSize, i / mazeSize);
    }

    private int getIndex(Coord coord) {
        return getIndex(coord.getX(), coord.getY());
    }

    public void setDoNothingCost(int doNothingCost) {
        this.doNothingCost = doNothingCost;
    }

    public void setTurnCost(int turnCost) {
        this.turnCost = turnCost;
    }

    public void setMoveCost(int moveCost) {
        this.moveCost = moveCost;
    }

    public void setNumberOfSimulationSteps(int numberOfSimulationSteps) {
        numberOfSimulateSteps = numberOfSimulationSteps;
    }

    public void setFoodMin(int foodMin) {
        foodMinVal = foodMin;
    }

    public void setFoodMax(int foodMax) {
        foodMaxVal = foodMax;
    }

    public void setFoodDecrease(int foodDecrease) {
        foodValueDecrease = foodDecrease;
    }


    public void setMazeSize(int mazeSize) {
        this.mazeSize = mazeSize;
    }

}
