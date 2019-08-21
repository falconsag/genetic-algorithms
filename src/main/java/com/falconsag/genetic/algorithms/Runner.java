package com.falconsag.genetic.algorithms;


import com.falconsag.genetic.Population;

public class Runner {
    public static void main(String[] args) {
        AllOnesGA ga = new AllOnesGA(100, 0.001, 0.99, 0);
        Population population = ga.initPopulation(15);
        ga.evalPopulation(population);

        int generationCounter = 1;
        while (!ga.isTerminationConditionMet(population)) {
            System.out.println(String.format("Fittest chromosome is: " + population.getNthFittest(0)));
            System.out.println(String.format("Population fitness is: " + population.getPopulationFitness()));
            population = ga.crossoverPopulation(population);
            population = ga.mutatePopulation(population);

            generationCounter++;
            ga.evalPopulation(population);
        }

        System.out.println(String.format("Found solution in %d generation(s)", generationCounter));
        System.out.println(String.format("Best solution is: %s", population.getNthFittest(0).toString()));
    }

}
