package com.falconsag.genetic.algorithms;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.jenetics.EliteSelector;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.MonteCarloSelector;
import io.jenetics.Optimize;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.PermutationChromosome;
import io.jenetics.Phenotype;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RobotRunner {


    public static void main(String[] args) {


//        Engine<BitGene, Integer> engine = Engine.builder(i -> {
//                                                             Chromosome<BitGene> chromosome = i.getChromosome();
//                                                             chromosome.as(BitChromosome.class).getGene().ge
//                                                             return chromosome.as(BitChromosome.class).bitCount();
//                                                         },
//                                                         BitChromosome.of(1000000, 0.15))
//                                                .populationSize(100)
//                                                .selector(new RouletteWheelSelector<>())
//                                                .survivorsSelector(new EliteSelector<>(1))
//                                                .alterers(new Mutator<>(0.01),
//                                                          new SinglePointCrossover<>(0.01))
//                                                .build();
////
//        EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();
//
//        Phenotype<BitGene, Integer> collect = engine.stream()
//                                                    .limit(Limits.byExecutionTime(Duration.of(50, ChronoUnit.SECONDS)))
//                                                    .peek(statistics)
//                                                    .collect(toBestPhenotype());
//
//        System.out.println(collect);
//        System.out.println(statistics);


//        IntegerGene of = IntegerGene.of(10, 1, 10);




        SpringApplication.run(RobotRunner.class, args);

    }





}
