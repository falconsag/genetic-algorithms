package com.falconsag.nqueen;

import com.falconsag.nqueen.solvers.BackTrackingSolver;
import com.falconsag.nqueen.solvers.NQueenSolver;
import com.falconsag.nqueen.solvers.PrePermutedSolver;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NQueenApplication {

    public static void main(String[] args) {
//        IntStream.range(0, 50).forEach(value -> executeMeasured(new PrePermutedSolver()));
        IntStream.rangeClosed(6, 40).forEach(value -> executeMeasuredCustom(new BackTrackingSolver(), value));
    }

    private static void executeMeasuredCustom(BackTrackingSolver solver,int size) {
        ImmutableSet<Integer> rangeSet = ImmutableSet.<Integer>builder().addAll(IntStream.rangeClosed(1, size).boxed().collect(Collectors.toList())).build();
        Stopwatch timer = Stopwatch.createStarted();
        List<Integer> solutionForSize = solver.getCustom(size, rangeSet);
        System.out.println(timer);
    }

    private static void executeMeasured(NQueenSolver solver) {
        Stopwatch timer = Stopwatch.createStarted();
        List<Integer> solutionForSize = solver.getSolutionForSize(20);
        System.out.println(timer);
    }

}
