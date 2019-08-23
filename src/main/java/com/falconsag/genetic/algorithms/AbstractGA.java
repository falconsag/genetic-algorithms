package com.falconsag.genetic.algorithms;

import com.falconsag.genetic.model.Chromosome;
import com.falconsag.genetic.model.Population;

public abstract class AbstractGA {
    protected int populationSize;
    protected double mutationRate;
    protected double crossoverRate;
    protected int elitismCount;

    public AbstractGA(int populationSize, double mutationRate, double crossoverRate, int elitismCount) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
    }

    public abstract Population initPopulation(int genesCount);

    public abstract double calcFitness(Chromosome chromosome);

    public abstract void evalPopulation(Population population);

    public abstract boolean isTerminationConditionMet(Population population);

    public abstract Chromosome selectParent(Population population);

    public abstract Population crossoverPopulation(Population population);

    public abstract Population mutatePopulation(Population population);
}
