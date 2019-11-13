package com.falconsag.genetic.algorithms.controllers;

import com.falconsag.genetic.algorithms.model.GeneticConfiguration;
import com.falconsag.genetic.algorithms.model.Phenotype;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.jenetics.EliteSelector;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.MonteCarloSelector;
import io.jenetics.Optimize;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.PermutationChromosome;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NQueenController {

    private ConcurrentLinkedDeque<Phenotype> simulations = new ConcurrentLinkedDeque<>();

    @GetMapping("/queen")
    public String hi() {
        return "queen";
    }


    @GetMapping(value = "/queen/getPhenotype", produces = "application/json")
    @ResponseBody
    public Phenotype getSimulations() {
        if (!simulations.isEmpty()) {
            return simulations.getLast();
        }
        return new Phenotype(-1, null, false, -1);
    }

    @PostMapping(value = "/queen/evolve", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public void postForGameStates(@RequestBody GeneticConfiguration config) {
        simulations = new ConcurrentLinkedDeque<>();//
        Engine<EnumGene<Integer>, Integer> build = Engine.builder(NQueenController::eval,
                                                                  Genotype.of(PermutationChromosome.ofInteger(1, config.getEditorConfig().getMazeSize() + 1)))
                                                         .populationSize(500)
                                                         .maximalPhenotypeAge(3)
                                                         .optimize(Optimize.MINIMUM)
                                                         .selector(new EliteSelector<>(3))
//                                                         .selector(new TournamentSelector<>(5))
                                                         .selector(new MonteCarloSelector<>())
                                                         .alterers(new SwapMutator<>(0.2), new PartiallyMatchedCrossover<>(0.35))
                                                         .build();


        int genLimit = config.getSimulatorConfig().getGenLimit();
        AtomicInteger gen = new AtomicInteger(0);
        io.jenetics.Phenotype<EnumGene<Integer>, Integer> lastPhenotype = build.stream()
                                                                               .limit(Limits.byFitnessThreshold(1))
                                                                               .limit(i -> {
                                                                                   if (genLimit == -1) {
                                                                                       return true;
                                                                                   } else if (i.getGeneration() >= genLimit) {
                                                                                       return false;
                                                                                   }
                                                                                   return true;
                                                                               })
                                                                               .peek(i -> {
                                                                                   gen.set((int) i.getGeneration());
                                                                                   io.jenetics.Phenotype<EnumGene<Integer>, Integer> bestPhenotype = i.getBestPhenotype();
                                                                                   simulations.add(new Phenotype(bestPhenotype.getFitness(), getGenesFomGenotype(bestPhenotype.getGenotype()), false, gen.get()));
                                                                                   System.out.println("#" + gen.get() + " :" + bestPhenotype);
                                                                               })
                                                                               .collect(EvolutionResult.toBestPhenotype());

        simulations.add(new Phenotype(lastPhenotype.getFitness(), getGenesFomGenotype(lastPhenotype.getGenotype()), true, gen.get()));
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

    private static Integer eval(Genotype<EnumGene<Integer>> i) {
        List<Integer> l = getGenesFomGenotype(i);
        return getNumberOfThreats(l, true);
    }

    private static List<Integer> getGenesFomGenotype(Genotype<EnumGene<Integer>> i) {
        int size = i.getChromosome().as(PermutationChromosome.class).getValidAlleles().size();
        List<Integer> l = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            l.add(i.getChromosome().getGene(j).getAllele());
        }
        return l;
    }
}



