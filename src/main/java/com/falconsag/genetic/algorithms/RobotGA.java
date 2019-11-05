package com.falconsag.genetic.algorithms;

import com.falconsag.genetic.algorithms.model.Chromosome;
import com.falconsag.genetic.algorithms.model.Population;
import com.falconsag.genetic.algorithms.model.robot.GameState;
import com.falconsag.genetic.algorithms.robot.Evaluator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class RobotGA extends AbstractGA {

    public static final int TRY_NUMBER = 1;

    public RobotGA(int populationSize, double mutationRate, double crossoverRate, int elitismCount) {
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
        return 0;
    }

    public double calcFitness(Chromosome chromosome, List<GameState> gameStates) {
        double sumFitness = 0;
        double bestFitness = 0;
        for (int i = 0; i < TRY_NUMBER; i++) {
            ArrayList<GameState> currentGameStates = new ArrayList<>();
            double fitness = new Evaluator(chromosome).evaluate(currentGameStates);
            sumFitness += fitness;
            if (fitness >= bestFitness) {
                bestFitness = fitness;
                gameStates.clear();
                gameStates.addAll(currentGameStates);
            }
        }
        double avgFitness = sumFitness / (double) TRY_NUMBER;
        chromosome.setFitness(avgFitness);
        return avgFitness;
    }

    @Override
    public void evalPopulation(Population population) {

    }

    public void evalPopulationForStates(Population population, List<GameState> states) {
        List<ImmutablePair<Double, List<GameState>>> collect = population.getChromosomes().stream().map(chromosome -> {
            List<GameState> gameStates = new ArrayList<>();
            calcFitness(chromosome, gameStates);
            return new ImmutablePair<>(chromosome, gameStates);
        }).sorted(Comparator.comparing(p -> p.getLeft().getFitness(), Comparator.reverseOrder())).map(i -> ImmutablePair.of(i.left.getFitness(), i.right)).collect(Collectors.toList());

        Optional<ImmutablePair<Double, List<GameState>>> reduced = collect.stream().reduce((i, j) -> ImmutablePair.of(i.left + j.left, i.right));

        double populationFitness = reduced.get().left;
        population.setPopulationFitness(populationFitness);
        states.addAll(reduced.get().right);
    }

    @Override
    public boolean isTerminationConditionMet(Population population) {
        return false;
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
        List<Chromosome> chromosomes = population.getChromosomes();
        for (int chromosomeIndex = 0; chromosomeIndex < chromosomes.size(); chromosomeIndex++) {
            Chromosome parent1 = chromosomes.get(chromosomeIndex);
            if (crossoverRate > Math.random() && (chromosomeIndex > elitismCount-1)) {
                Chromosome parent2 = selectParent(population);
                Chromosome offspring = singlePointCrossover(parent1, parent2);
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
                if (Math.random() < mutationRate && i > elitismCount-1) {
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

    private Chromosome singlePointCrossover(Chromosome parent1, Chromosome parent2) {
        List<Integer> genes1 = parent1.getGenes();
        List<Integer> genes2 = parent2.getGenes();
        int swapPoint = (int) (Math.random() * (genes1.size() + 1));
        List<Integer> offspringGenes = new ArrayList<>();
        for (int geneIndex = 0; geneIndex < genes1.size(); geneIndex++) {
            if (geneIndex < swapPoint) {
                offspringGenes.add(genes1.get(geneIndex));
            } else {
                offspringGenes.add(genes2.get(geneIndex));
            }
        }
        return new Chromosome(offspringGenes);
    }

    public double getAvgFitness(Population population) {
        return population.getChromosomes().stream().mapToDouble(chromosome -> chromosome.getFitness()).average().orElse(0);
    }
}
