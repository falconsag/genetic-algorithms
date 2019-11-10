package com.falconsag.genetic.algorithms.model;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Chromosome {

    private List<Integer> genes;
    private List<Integer> decider;
    private double fitness = -1;

    public Chromosome(int geneLength) {
        genes = new Random().ints(geneLength, 0, 2).boxed().collect(Collectors.toList());
        decider = new Random().ints(2, 0, 2).boxed().collect(Collectors.toList());
    }

    public Chromosome(List<Integer> genes) {
        this.genes = genes;
    }

    public Chromosome(List<Integer> genes, List<Integer> decider) {
        this.genes = genes;
        this.decider = decider;
    }


    @Override
    public String toString() {
        return "Chromosome{" +
                "fitness=" + fitness +
                ", genes=" + genes +
                '}';
    }

    public double getFitness() {
        return fitness;
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public List<Integer> getDecider() {
        return decider;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void flipGene(int index) {
        if (index >= genes.size()) {
            int deciderIndex = genes.size() - index;
            decider.set(deciderIndex, decider.get(deciderIndex) ^ 1);
        } else {
            genes.set(index, genes.get(index) ^ 1);
        }
    }
}
