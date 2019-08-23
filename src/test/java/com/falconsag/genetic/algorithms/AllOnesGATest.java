package com.falconsag.genetic.algorithms;

import com.falconsag.genetic.model.Chromosome;
import com.falconsag.genetic.model.Population;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.iterators.PermutationIterator;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AllOnesGATest {

    public static final double ERROR = 0.01;
    private final AllOnesGA ga;

    public AllOnesGATest() {
        ga = new AllOnesGA(0, 0, 0, 0);
    }

    @Test
    public void testCalcFitness() {
        assertThat(ga.calcFitness(new Chromosome(Arrays.asList(0, 1, 1, 1))), closeTo(0.75, ERROR));
        assertThat(ga.calcFitness(new Chromosome(Arrays.asList(0, 0, 0, 0))), closeTo(0, ERROR));

        Chromosome chromosome = new Chromosome(Arrays.asList(1, 1, 1, 1, 1));
        assertThat(ga.calcFitness(chromosome), closeTo(1, ERROR));
        assertThat(chromosome.getFitness(), comparesEqualTo(Double.valueOf(1.0)));
    }

    @Test
    public void testEvalPopulation() {
        Population population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(0, 0, 0)),
                new Chromosome(Arrays.asList(0, 0, 0)),
                new Chromosome(Arrays.asList(0, 0, 0))));
        ga.evalPopulation(population);
        assertThat(population.getPopulationFitness(), closeTo(0, ERROR));

        population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(0, 0, 1)),
                new Chromosome(Arrays.asList(0, 0, 1)),
                new Chromosome(Arrays.asList(0, 0, 1))));
        ga.evalPopulation(population);
        assertThat(population.getPopulationFitness(), closeTo(1, ERROR));

        population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(1, 1, 1)),
                new Chromosome(Arrays.asList(1, 1, 1)),
                new Chromosome(Arrays.asList(1, 1, 1))));
        ga.evalPopulation(population);
        assertThat(population.getPopulationFitness(), closeTo(3, ERROR));
    }

    @Test
    public void testIsTerminationConditionMet() {
        Population population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(0, 0, 0)),
                new Chromosome(Arrays.asList(0, 0, 0)),
                new Chromosome(Arrays.asList(0, 0, 0))));
        ga.evalPopulation(population);
        assertFalse(ga.isTerminationConditionMet(population));
        population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(1, 1, 1)),
                new Chromosome(Arrays.asList(0, 0, 1)),
                new Chromosome(Arrays.asList(0, 0, 1))));
        ga.evalPopulation(population);
        assertTrue(ga.isTerminationConditionMet(population));
        population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(0, 1, 1)),
                new Chromosome(Arrays.asList(1, 0, 1)),
                new Chromosome(Arrays.asList(1, 0, 1))));
        ga.evalPopulation(population);
        assertFalse(ga.isTerminationConditionMet(population));
    }


    @Test
    public void testPopulationAvgFitness() {
        Population population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(0, 0, 0)),
                new Chromosome(Arrays.asList(0, 0, 0)),
                new Chromosome(Arrays.asList(0, 0, 0))));
        ga.evalPopulation(population);
        assertThat(ga.getAvgFitness(population), is(Double.valueOf(0)));
        population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(1, 1, 1)),
                new Chromosome(Arrays.asList(0, 0, 1)),
                new Chromosome(Arrays.asList(0, 0, 1))));
        ga.evalPopulation(population);
        assertThat(ga.getAvgFitness(population), closeTo(0.555, ERROR));
        population = new Population(Arrays.asList(
                new Chromosome(Arrays.asList(0, 1, 1)),
                new Chromosome(Arrays.asList(1, 0, 1)),
                new Chromosome(Arrays.asList(1, 0, 1))));
        ga.evalPopulation(population);
        assertThat(ga.getAvgFitness(population), closeTo((double) 2 / 3, ERROR));
    }

}