package com.falconsag.genetic.model;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Chromosome {

    private List<Integer> genes;
    private double fitness = -1;

    public Chromosome(int geneLength) {
        genes = new Random().ints(geneLength, 0, 2).boxed().collect(Collectors.toList());
    }

    public Chromosome(List<Integer> genes) {
        this.genes = genes;
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "genes=" + genes +
                ", fitness=" + fitness +
                '}';
    }

    public double getFitness() {
        return fitness;
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void flipGene(int index){
        genes.set(index, genes.get(index) ^ 1);
    }
}
