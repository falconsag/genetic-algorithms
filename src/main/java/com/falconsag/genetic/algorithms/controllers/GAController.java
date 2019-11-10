package com.falconsag.genetic.algorithms.controllers;

import com.falconsag.genetic.algorithms.RobotGA;
import com.falconsag.genetic.algorithms.model.Chromosome;
import com.falconsag.genetic.algorithms.model.GeneticConfiguration;
import com.falconsag.genetic.algorithms.model.Population;
import com.falconsag.genetic.algorithms.model.robot.GameSimulation;
import com.falconsag.genetic.algorithms.model.robot.Simulation;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GAController {
    private static final int GENE_LENGTH = (int) Math.pow(2, 12);
    private ConcurrentLinkedDeque<GameSimulation> simulations;

    @GetMapping("/")
    public String hi() {
        return "welcome";
    }

    private List<Double> fitnessValues;


    public Integer count(Genotype<BitGene> gt) {
        return gt.getChromosome().as(BitChromosome.class).bitCount();
    }


    @GetMapping(value = "/getSimulations", produces = "application/json")
    @ResponseBody
    public List<Double> getSimulations(){
        return simulations.stream().map(i->i.getFitness()).collect(Collectors.toList());
    }

    @PostMapping(value = "/evolve", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Simulation postForGameStates(@RequestBody GeneticConfiguration config) {
        simulations = new ConcurrentLinkedDeque<>();
        List<Double> fitnessValues = new ArrayList<>();
        RobotGA ga = new RobotGA(100, 0.01, 0.99, 2);
        Population population = ga.initPopulation(GENE_LENGTH);
        ga.evalPopulationForStates(population, simulations, 1, config);
        int generationCounter = 1;

        long sumEval = 0;
        long sumMutate = 0;
        long sumCrossover = 0;

        int genLimit = config.getSimulatorConfig().getGenLimit();
        long prevT = getCurrentMillis();
        while (!ga.isTerminationConditionMet(population)) {
            if (!(generationCounter < genLimit)) break;
            Chromosome fittest = population.getNthFittest(0);
            fitnessValues.add(fittest.getFitness());

            long l = getCurrentMillis();
            population = ga.crossoverPopulation(population);
            sumCrossover += (getCurrentMillis() - l);

            l = getCurrentMillis();
            population = ga.mutatePopulation(population);
            sumMutate += (getCurrentMillis() - l);

            ++generationCounter;
            if (generationCounter % 10 == 0) {
                long now = getCurrentMillis();
                long procMs = now - prevT;
                OffsetDateTime nowOD = OffsetDateTime.now();
                System.out.println("" + generationCounter + " :" + procMs + "ms");

                int rem = (genLimit - generationCounter) / 10;
                double remSecs = (rem * procMs) / (double) 1000;
                System.out.println("estimated remaining time: " + nowOD.plusSeconds((int) remSecs));
                System.out.println("Fittest chromosome is: " + fittest.getFitness());
                prevT = now;
            }

            l = getCurrentMillis();
            ga.evalPopulationForStates(population, simulations, 1, config);
            sumEval += (getCurrentMillis() - l);
        }

        System.out.println(sumEval);
        System.out.println(sumCrossover);
        System.out.println(sumMutate);

        return new Simulation(config.getEditorConfig().getMapArr(), simulations.getLast().getGameStates(),
                              simulations.stream().map(i -> i.getFitness()).collect(Collectors.toList()),
                              config.getSimulatorConfig().getFoodMax(),
                              config.getSimulatorConfig().getFoodDecrease());

    }

    private long getCurrentMillis() {
        return System.currentTimeMillis();
    }
}
