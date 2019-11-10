package com.falconsag.genetic.algorithms;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Chromosome;
import io.jenetics.EliteSelector;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.engine.Engine;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
        SpringApplication.run(RobotRunner.class, args);

    }
}
