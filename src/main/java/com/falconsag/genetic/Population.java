package com.falconsag.genetic;

import java.awt.Choice;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Population {

    private double populationFitness = -1;
    private int populationSize;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private List<Chromosome> chromosomes = new ArrayList<>();

    public Population(int populationSize, int geneLength) {
        this.populationSize = populationSize;
        chromosomes = IntStream.rangeClosed(1, populationSize)
                               .mapToObj(value -> new Chromosome(geneLength))
                               .collect(Collectors.toList());
    }

    public Population() {
    }

    public void addChromosome(Chromosome chromosome) {
        chromosomes.add(chromosome);
        populationSize = chromosomes.size();
    }

    public Population(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
        this.populationSize = chromosomes.size();
    }


    @Override
    public String toString() {
        return "Population{" +
                "populationSize=" + populationSize + LINE_SEPARATOR +
                chromosomes.stream().map(Chromosome::toString).collect(Collectors.joining(LINE_SEPARATOR)) +
                '}';
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public Chromosome getNthFittest(int n) {
        sortPopulationByFitness();
        return chromosomes.get(n);
    }

    public void sortPopulationByFitness() {
        chromosomes.sort((o1, o2) -> (int) Math.signum(o2.getFitness() - o1.getFitness()));
    }

    public void setPopulationFitness(double populationFitness) {
        this.populationFitness = populationFitness;
    }

    public double getPopulationFitness() {
        return populationFitness;
    }

    public int getPopulationSize() {
        return populationSize;
    }

}
