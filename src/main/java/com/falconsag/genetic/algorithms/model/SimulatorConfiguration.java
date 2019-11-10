package com.falconsag.genetic.algorithms.model;

public class SimulatorConfiguration {

    private int genLimit;
    private int numberOfSimulateSteps;
    private int moveCost;
    private int turnCost;
    private int foodMin;
    private int foodMax;
    private int foodDecrease;
    private int numberOfSimulationsToAvg;
    private int doNothingCost;

    public int getGenLimit() {
        return genLimit;
    }

    public void setGenLimit(int genLimit) {
        this.genLimit = genLimit;
    }

    public int getNumberOfSimulateSteps() {
        return numberOfSimulateSteps;
    }

    public void setNumberOfSimulateSteps(int numberOfSimulateSteps) {
        this.numberOfSimulateSteps = numberOfSimulateSteps;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public void setMoveCost(int moveCost) {
        this.moveCost = moveCost;
    }

    public int getTurnCost() {
        return turnCost;
    }

    public void setTurnCost(int turnCost) {
        this.turnCost = turnCost;
    }

    public int getFoodMin() {
        return foodMin;
    }

    public void setFoodMin(int foodMin) {
        this.foodMin = foodMin;
    }

    public int getFoodMax() {
        return foodMax;
    }

    public void setFoodMax(int foodMax) {
        this.foodMax = foodMax;
    }

    public int getFoodDecrease() {
        return foodDecrease;
    }

    public void setFoodDecrease(int foodDecrease) {
        this.foodDecrease = foodDecrease;
    }

    public int getNumberOfSimulationsToAvg() {
        return numberOfSimulationsToAvg;
    }

    public void setNumberOfSimulationsToAvg(int numberOfSimulationsToAvg) {
        this.numberOfSimulationsToAvg = numberOfSimulationsToAvg;
    }

    public int getDoNothingCost() {
        return doNothingCost;
    }

    public void setDoNothingCost(int doNothingCost) {
        this.doNothingCost = doNothingCost;
    }
}
