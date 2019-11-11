package com.falconsag.genetic.algorithms.model.robot;

import com.falconsag.genetic.algorithms.model.Chromosome;
import java.util.List;

public class GameSimulation {
    private List<GameState> gameStates;
    private double fitness;
    private Chromosome chromosome;

    public GameSimulation(List<GameState> gameStates, double fitness, Chromosome chromosome) {
        this.gameStates = gameStates;
        this.fitness = fitness;
        this.chromosome = chromosome;
    }

    public Chromosome getChromosome() {
        return chromosome;
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
