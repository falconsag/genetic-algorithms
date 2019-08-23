package com.falconsag.nqueen;

import com.falconsag.nqueen.solvers.NQueenSolver;
import com.falconsag.nqueen.solvers.PrePermutedSolver;
import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.stream.IntStream;

public class NQueenApplication {

    public static void main(String[] args) {
        IntStream.range(0, 50).forEach(value -> executeMeasured(new PrePermutedSolver()));
    }

    private static void executeMeasured(NQueenSolver solver) {
        Stopwatch timer = Stopwatch.createStarted();
        List<Integer> solutionForSize = solver.getSolutionForSize(14);
        System.out.println(timer);
    }


}
