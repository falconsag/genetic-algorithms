package com.falconsag.nqueen.solvers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.iterators.PermutationIterator;

/**
 * Solves the n queen problem by pre permutating all possible cases and checking threats
 * For threat checking contraint propagation is used. If strict threat checking is set, then even if there's
 * already a threat, it continues to count all the threats
 */
public class PrePermutedSolver implements NQueenSolver {

    @Override
    public List<Integer> getSolutionForSize(int size) {
        PermutationIterator<Integer> permutationIterator = new PermutationIterator(IntStream.rangeClosed(1, size).boxed().collect(Collectors.toList()));

        List<Integer> solution = null;
        while (permutationIterator.hasNext() && solution == null) {
            List<Integer> chromosome = permutationIterator.next();
            int numberOfThreats = getNumberOfThreats(chromosome, false);
            if (numberOfThreats == 0) {
                solution = chromosome;
            }
        }
        return solution;
    }

    private List<Integer> generateRandomChromosome(Random random, int size) {
        List<Integer> chromosome = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            chromosome.add(random.nextInt(size) + 1);
        }
        return chromosome;
    }

    /**
     * In the chromosomes positions use 1 index as start, therefore we need the +1 at places
     * Coordinate system is as follows:
     * <p>
     * Chromosome: 2 3 4 1
     * <p>
     * 4|       x
     * 3|    x
     * 2|x
     * 1|         x
     *   ----------
     *   1  2  3  4
     *
     * @param chromosome
     * @param strict     if set returns exactly how many threats are present, otherwise returns only the existence of a threat
     *                   meaning it's not a valid solution
     * @return
     */
    private int getNumberOfThreats(List<Integer> chromosome, boolean strict) {
        int threats = 0;
        int size = chromosome.size();
        for (int col = 1; col <= size; col++) {
            final int row = chromosome.get(col - 1);
            Multimap<Integer, Integer> threatMap = generateThreatMapForPosition(size, col, row);
            for (int j = col; j < size; j++) {
                Integer testedChromosomeRow = chromosome.get(j);
                Integer testedChromosomeCol = j + 1;
                if (threatMap.get(testedChromosomeCol).contains(testedChromosomeRow)) {
                    threats++;
                    if (!strict) {
                        return 1;
                    }
                }
            }
        }
        return threats;
    }

    /**
     * Creates a Multimap of the threats by a queen placed at col,row on a table of size
     * Map returns that for each column, which row indexes are threatened by the position of colPos,rowPos
     *
     * @param size
     * @param colPos
     * @param rowPos
     * @return
     */
    private Multimap<Integer, Integer> generateThreatMapForPosition(int size, int colPos, int rowPos) {
        int maxNeededIterations = size - colPos;
        Multimap<Integer, Integer> threatPositions = ArrayListMultimap.create();
        IntStream.rangeClosed(1, maxNeededIterations)
                 .forEach(increment -> {
                     int targetColPos = colPos + increment;
                     threatPositions.put(targetColPos, rowPos);
                     if (rowPos + increment <= size) {
                         threatPositions.put(targetColPos, rowPos + increment);
                     }
                     if (rowPos - increment > 0) {
                         threatPositions.put(targetColPos, rowPos - increment);
                     }
                 });
        return threatPositions;
    }

}
