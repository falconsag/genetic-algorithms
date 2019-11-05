package com.falconsag.genetic.algorithms;

import com.falconsag.genetic.algorithms.model.Chromosome;
import com.falconsag.genetic.algorithms.model.Population;
import java.util.ArrayList;
import java.util.List;

public class AllOnesGA extends AbstractGA {

    public AllOnesGA(int populationSize, double mutationRate, double crossoverRate, int elitismCount) {
        super(populationSize, mutationRate, crossoverRate, elitismCount);
    }

    private static boolean isOne(Integer integer) {
        return integer == 1;
    }

    @Override
    public Population initPopulation(int geneLength) {
        return new Population(this.populationSize, geneLength);
    }

    @Override
    public double calcFitness(Chromosome chromosome) {
        double fitness = chromosome.getGenes().stream().filter(AllOnesGA::isOne).count() / (double) chromosome.getGenes().size();
        chromosome.setFitness(fitness);
        return fitness;
    }

    @Override
    public void evalPopulation(Population population) {
        double populationFitness = population.getChromosomes().stream().mapToDouble(chromosome -> calcFitness(chromosome)).sum();
        population.setPopulationFitness(populationFitness);
    }

    @Override
    public boolean isTerminationConditionMet(Population population) {
        return population.getChromosomes().stream().anyMatch(chromosome -> chromosome.getFitness() == 1);
    }

    @Override
    public Chromosome selectParent(Population population) {
        double populationFitness = population.getPopulationFitness();
        double rouletteWheelPosition = Math.random() * populationFitness;

        double spinWheel = 0;
        List<Chromosome> chromosomes = population.getChromosomes();
        for (Chromosome chromosome : chromosomes) {
            spinWheel += chromosome.getFitness();
            if (spinWheel >= rouletteWheelPosition) {
                return chromosome;
            }
        }

        return chromosomes.get(chromosomes.size() - 1);
    }

    @Override
    public Population crossoverPopulation(Population population) {
        Population newPopulation = new Population();
        population.sortPopulationByFitness();
        for (Chromosome parent1 : population.getChromosomes()) {
            //TODO implement elitism count
            if (crossoverRate > Math.random()) {
                Chromosome parent2 = selectParent(population);
                Chromosome offspring = crossoverChromosomes(parent1, parent2);
                newPopulation.addChromosome(offspring);
            } else {
                newPopulation.addChromosome(parent1);
            }
        }
        return newPopulation;
    }

    @Override
    public Population mutatePopulation(Population population) {
        for (Chromosome chromosome : population.getChromosomes()) {
            for (int i = 0; i < chromosome.getGenes().size(); i++) {
                if(Math.random() < mutationRate){
                    chromosome.flipGene(i);
                }
            }
        }
        return population;
    }

    private Chromosome crossoverChromosomes(Chromosome parent1, Chromosome parent2) {
        List<Integer> genes1 = parent1.getGenes();
        List<Integer> genes2 = parent2.getGenes();

        List<Integer> offspringGenes = new ArrayList<>();
        for (int i = 0; i < genes1.size(); i++) {
            if (Math.random() > 0.5) {
                offspringGenes.add(genes1.get(i));
            } else {
                offspringGenes.add(genes2.get(i));
            }
        }

        return new Chromosome(offspringGenes);
    }

    public double getAvgFitness(Population population) {
        return population.getChromosomes().stream().mapToDouble(chromosome -> chromosome.getFitness()).average().orElse(0);
    }
}
