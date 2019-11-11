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


        Engine<EnumGene<Integer>, Integer> build = Engine.builder(i -> {
                                                                      int size = i.getChromosome().as(PermutationChromosome.class).getValidAlleles().size();
                                                                      List<Integer> l = new ArrayList<>();
                                                                      for (int j = 0; j < size; j++) {
                                                                          l.add(i.getChromosome().getGene(j).getAllele());
                                                                      }
                                                                      return getNumberOfThreats(l, true);
                                                                  },
                                                                  Genotype.of(PermutationChromosome.ofInteger(1, 50)))
                                                         .populationSize(300)
                                                         .maximalPhenotypeAge(3)
                                                         .optimize(Optimize.MINIMUM)
                                                         .selector(new EliteSelector<>(5))
//                                                         .selector(new TournamentSelector<>(5))
//                                                         .selector(new MonteCarloSelector<>())
                                                         .alterers(new SwapMutator<>(0.2), new PartiallyMatchedCrossover<>(0.35))
                                                         .build();


        Phenotype<EnumGene<Integer>, Integer> collect = build.stream()
                                                             .limit(Limits.byFitnessThreshold(1))
                                                             .peek(i -> {
                                                                 System.out.println(i.getBestPhenotype());

                                                             })
                                                             .collect(EvolutionResult.toBestPhenotype());
        System.out.println(collect);

//        SpringApplication.run(RobotRunner.class, args);

    }


    private static int getNumberOfThreats(List<Integer> chromosome, boolean strict) {
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


    private static Multimap<Integer, Integer> generateThreatMapForPosition(int size, int colPos, int rowPos) {
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
