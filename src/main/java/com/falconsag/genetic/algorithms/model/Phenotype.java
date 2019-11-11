package com.falconsag.genetic.algorithms.model;

import com.falconsag.genetic.algorithms.model.robot.GameSimulation;
import java.util.List;

public class Phenotype {
    private double fitness;
    private List<Integer> genes;

    public Phenotype(double fitness, List<Integer> genes) {
        this.fitness = fitness;
        this.genes = genes;
    }

    public Phenotype(GameSimulation gameSimulation) {
        this.fitness = gameSimulation.getFitness();
        this.genes = gameSimulation.getChromosome().getGenes();
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }
}
