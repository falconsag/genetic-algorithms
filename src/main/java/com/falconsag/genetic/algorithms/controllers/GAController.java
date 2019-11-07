package com.falconsag.genetic.algorithms.controllers;

import com.falconsag.genetic.algorithms.RobotGA;
import com.falconsag.genetic.algorithms.model.Chromosome;
import com.falconsag.genetic.algorithms.model.EditorConfiguration;
import com.falconsag.genetic.algorithms.model.Population;
import com.falconsag.genetic.algorithms.model.robot.Coord;
import com.falconsag.genetic.algorithms.model.robot.GameState;
import com.falconsag.genetic.algorithms.model.robot.Simulation;
import com.falconsag.genetic.algorithms.robot.Evaluator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GAController {
    private static final int GENE_LENGTH = (int) Math.pow(2, 12);

    @GetMapping("/")
    public String hi() {
        return "welcome";
    }


    @PostMapping(value = "/evolve", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Simulation postForGameStates(@RequestParam(value = "genLimit") int genLimit,
                                        @RequestParam(value = "numberOfSimulateSteps") int numberOfSimulateSteps,
                                        @RequestParam(value = "moveCost") int moveCost,
                                        @RequestParam(value = "turnCost") int turnCost,
                                        @RequestParam(value = "foodMin") int foodMin,
                                        @RequestParam(value = "foodMax") int foodMax,
                                        @RequestParam(value = "foodDecrease") int foodDecrease,
                                        @RequestParam(value = "numberOfSimulationsToAvg") int numberOfSimulationsToAvg,
                                        @RequestParam(value = "doNothingCost") int doNothingCost,
                                        @RequestBody EditorConfiguration editorConfig) {
        Evaluator.setMoveCost(moveCost);
        Evaluator.setTurnCost(turnCost);
        Evaluator.setDoNothingCost(doNothingCost);
        Evaluator.setNumberOfSimulationSteps(numberOfSimulateSteps);
        Evaluator.setFoodMin(foodMin);
        Evaluator.setFoodMax(foodMax);
        Evaluator.setFoodDecrease(foodDecrease);
        Evaluator.init(editorConfig.getMapArr().stream().mapToInt(Integer::intValue).toArray(),
                       new Coord(editorConfig.getRobot().getX(), editorConfig.getRobot().getY()),
                       new Coord(editorConfig.getRobot().getDirX(), editorConfig.getRobot().getDirY()),
                       editorConfig.getMazeSize()
        );


        List<Double> fitnessValues = new ArrayList<>();
        RobotGA ga = new RobotGA(100, 0.01, 0.99, 2);
        Population population = ga.initPopulation(GENE_LENGTH);
        List<GameState> gameStates = new ArrayList<>();
        ga.evalPopulationForStates(population, gameStates, numberOfSimulationsToAvg);
        int generationCounter = 1;

        long sumEval = 0;
        long sumMutate = 0;
        long sumCrossover = 0;

        long prevT = getCurrentMillis();
        while (!ga.isTerminationConditionMet(population) && generationCounter < genLimit) {
            Chromosome nthFittest = population.getNthFittest(0);

            Chromosome fittest = population.getNthFittest(0);
            System.out.println("Fittest chromosome is: " + fittest.getFitness());
            fitnessValues.add(fittest.getFitness());
//            System.out.println("Population fitness is: " + population.getPopulationFitness());

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
                prevT = now;
            }
//            System.out.println(generationCounter);

            gameStates = new ArrayList<>();
            l = getCurrentMillis();
            ga.evalPopulationForStates(population, gameStates, numberOfSimulationsToAvg);
            sumEval += (getCurrentMillis() - l);

        }

        System.out.println(sumEval);
        System.out.println(sumCrossover);
        System.out.println(sumMutate);
//        System.out.println(String.format("Found solution in %d generation(s)", generationCounter));
//        System.out.println(String.format("Best solution is: %s", population.getNthFittest(0).toString()));


        return new Simulation(Evaluator.getField(), gameStates, fitnessValues);

    }

    private long getCurrentMillis() {
        return System.currentTimeMillis();
    }
}
