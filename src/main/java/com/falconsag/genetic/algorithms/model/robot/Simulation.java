package com.falconsag.genetic.algorithms.model.robot;

import com.falconsag.genetic.algorithms.robot.Evaluator;
import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Double> fitnessValues;
    private int[] fields;
    private List<GameState> states = new ArrayList<>();
    private int foodMaxValue = Evaluator.FOOD_MAX_VAL;
    private int foodDecreaseValue = Evaluator.FODD_VALUE_DECREASE;
    private int sightDistance = Evaluator.SIGHT_DISTANCE;

    public Simulation(int[] fields, List<GameState> states, List<Double> fitnessValues) {
        this.fields = fields;
        this.states = states;
        this.fitnessValues = fitnessValues;
    }

    public void addGameState(GameState gameState) {
        this.states.add(gameState);
    }

    public int[] getFields() {
        return fields;
    }

    public List<GameState> getStates() {
        return states;
    }

    public int getFoodMaxValue() {
        return foodMaxValue;
    }

    public int getFoodDecreaseValue() {
        return foodDecreaseValue;
    }

    public int getSightDistance() {
        return sightDistance;
    }

    public void setSightDistance(int sightDistance) {
        this.sightDistance = sightDistance;
    }

    public List<Double> getFitnessValues() {
        return fitnessValues;
    }
}
