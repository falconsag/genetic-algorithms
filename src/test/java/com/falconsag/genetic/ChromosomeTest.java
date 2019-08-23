package com.falconsag.genetic;

import com.falconsag.genetic.model.Chromosome;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

public class ChromosomeTest {

    @Test
    public void flipGene() {
        Chromosome chromosome = new Chromosome(Arrays.asList(1, 0, 0, 0));
        chromosome.flipGene(0);
        Iterables.all(chromosome.getGenes(), gene -> gene == 0);
        chromosome = new Chromosome(Arrays.asList(1, 1, 1, 0));
        chromosome.flipGene(3);
        Iterables.all(chromosome.getGenes(), gene -> gene == 1);
    }
}