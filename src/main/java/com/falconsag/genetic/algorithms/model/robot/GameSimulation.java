package com.falconsag.genetic.algorithms.model.robot;

import java.util.List;

public class GameSimulation {
    public List<GameState> gameStates;
    public double fitness;

    public GameSimulation(List<GameState> gameStates, double fitness) {
        this.gameStates = gameStates;
        this.fitness = fitness;
    }

    public List<GameState> getGameStates() {
        return gameStates;
    }

    public void setGameStates(List<GameState> gameStates) {
        this.gameStates = gameStates;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
