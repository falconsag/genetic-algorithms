package com.falconsag.genetic.algorithms.model.robot;

import com.falconsag.genetic.algorithms.robot.Evaluator;
import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Double> fitnessValues;
    private List<Integer> fields;
    private List<GameState> states = new ArrayList<>();
    private int foodMaxValue;
    private int foodDecreaseValue;
    private int sightDistance = Evaluator.SIGHT_DISTANCE;

    public Simulation(List<Integer> fields, List<GameState> states, List<Double> fitnessValues, int foodMaxValue, int foodDecreaseValue) {
        this.fields = fields;
        this.states = states;
        this.fitnessValues = fitnessValues;
        this.foodMaxValue = foodMaxValue;
        this.foodDecreaseValue = foodDecreaseValue;
    }

    public void addGameState(GameState gameState) {
        this.states.add(gameState);
    }

    public List<Integer> getFields() {
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
