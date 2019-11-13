package com.falconsag.genetic.algorithms.model;

import com.falconsag.genetic.algorithms.model.robot.GameSimulation;
import java.util.List;

public class Phenotype {
    private double fitness;
    private List<Integer> genes;
    private boolean finished = false;
    private Integer generation;

    public Phenotype(double fitness, List<Integer> genes) {
        this.fitness = fitness;
        this.genes = genes;
    }

    public Phenotype(double fitness, List<Integer> genes, boolean finished, Integer generation) {
        this.fitness = fitness;
        this.genes = genes;
        this.finished = finished;
        this.generation = generation;
    }

    public Phenotype(double fitness, List<Integer> genes, boolean finished) {
        this.fitness = fitness;
        this.genes = genes;
        this.finished = finished;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Integer getGeneration() {
        return generation;
    }

    public void setGeneration(Integer generation) {
        this.generation = generation;
    }
}
