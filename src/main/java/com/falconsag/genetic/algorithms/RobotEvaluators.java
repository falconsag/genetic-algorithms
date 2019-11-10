package com.falconsag.genetic.algorithms;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class RobotEvaluators {

    public static <G extends Gene<?, G>, C extends Comparable<? super C>>
    io.jenetics.engine.Evaluator<G, C> reevaluateEvaluator(
            final Function<? super Genotype<G>, ? extends C> fitness,
            final Executor executor
    ) {
        return new ReevaluateEvaluator<>(fitness, executor);
    }
}
