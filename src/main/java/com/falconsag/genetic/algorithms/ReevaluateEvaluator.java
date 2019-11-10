package com.falconsag.genetic.algorithms;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Evaluator;
import io.jenetics.internal.util.Concurrency;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.Executor;
import java.util.function.Function;

final class ReevaluateEvaluator<
        G extends Gene<?, G>,
        C extends Comparable<? super C> >
        implements Evaluator<G, C> {

    private final Function<? super Genotype<G>, ? extends C> _function;
    private final Executor _executor;

    public ReevaluateEvaluator(
            final Function<? super Genotype<G>, ? extends C> function,
            final Executor executor
    ) {
        _function = requireNonNull(function);
        _executor = requireNonNull(executor);
    }

    public ReevaluateEvaluator<G, C> with(final Executor executor) {
        return new ReevaluateEvaluator<>(_function, executor);
    }

    @Override
    public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
        final ISeq<ReevaluateEvaluator.PhenotypeFitness<G, C>> evaluate = population.stream()
//                                                                                    .filter(Phenotype::nonEvaluated)
                                                                                    .map(pt -> new ReevaluateEvaluator.PhenotypeFitness<>(pt, _function))
                                                                                    .collect(ISeq.toISeq());

        final ISeq<Phenotype<G, C>> result;
        if (evaluate.nonEmpty()) {
            try (Concurrency c = Concurrency.with(_executor)) {
                c.execute(evaluate);
            }

            result = evaluate.size() == population.size()
                    ? evaluate.map(ReevaluateEvaluator.PhenotypeFitness::phenotype)
                    : population.stream()
                                .filter(Phenotype::isEvaluated)
                                .collect(ISeq.toISeq())
                                .append(evaluate.map(ReevaluateEvaluator.PhenotypeFitness::phenotype));
        } else {
            result = population.asISeq();
        }

        return result;
    }


    private static final class PhenotypeFitness<
            G extends Gene<?, G>,
            C extends Comparable<? super C>
            >
            implements Runnable {
        final Phenotype<G, C> _phenotype;
        final Function<? super Genotype<G>, ? extends C> _function;
        C _fitness;

        PhenotypeFitness(
                final Phenotype<G, C> phenotype,
                final Function<? super Genotype<G>, ? extends C> function
        ) {
            _phenotype = phenotype;
            _function = function;
        }

        @Override
        public void run() {
            _fitness = _function.apply(_phenotype.getGenotype());
        }

        Phenotype<G, C> phenotype() {
            return _phenotype.withFitness(_fitness);
        }

    }

}
