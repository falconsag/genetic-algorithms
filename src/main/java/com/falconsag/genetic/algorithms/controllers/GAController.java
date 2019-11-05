package com.falconsag.genetic.algorithms.controllers;

import com.falconsag.genetic.algorithms.RobotGA;
import com.falconsag.genetic.algorithms.model.Chromosome;
import com.falconsag.genetic.algorithms.model.Population;
import com.falconsag.genetic.algorithms.model.robot.GameState;
import com.falconsag.genetic.algorithms.model.robot.Simulation;
import com.falconsag.genetic.algorithms.robot.Evaluator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GAController {
    private static final int GENE_LENGTH = (int) Math.pow(2, 12);

    @GetMapping("/")
    public String hi() {
        return "welcome";
    }

    @GetMapping(value = "/evolve", produces = "application/json")
    @ResponseBody
    public Simulation getGameStates(@RequestParam(value = "genLimit") int genLimit,
                                    @RequestParam(value = "numberOfSimulateSteps") int numberOfSimulateSteps,
                                    @RequestParam(value = "moveCost") int moveCost,
                                    @RequestParam(value = "turnCost") int turnCost,
                                    @RequestParam(value = "foodMin") int foodMin,
                                    @RequestParam(value = "foodMax") int foodMax,
                                    @RequestParam(value = "foodDecrease") int foodDecrease,
                                    @RequestParam(value = "mazeSize") int mazeSize,
                                    @RequestParam(value = "doNothingCost") int doNothingCost) {

        Evaluator.setMoveCost(moveCost);
        Evaluator.setTurnCost(turnCost);
        Evaluator.setDoNothingCost(doNothingCost);
        Evaluator.setNumberOfSimulationSteps(numberOfSimulateSteps);
        Evaluator.setFoodMin(foodMin);
        Evaluator.setFoodMax(foodMax);
        Evaluator.setMazeSize(mazeSize);
        Evaluator.setFoodDecrease(foodDecrease);

        RobotGA ga = new RobotGA(100, 0.001, 0.99, 2);
        Population population = ga.initPopulation(GENE_LENGTH);
        List<GameState> gameStates = new ArrayList<>();
        ga.evalPopulationForStates(population, gameStates);
        int generationCounter = 1;
        while (!ga.isTerminationConditionMet(population) && generationCounter < genLimit) {
            Chromosome nthFittest = population.getNthFittest(0);

            System.out.println("Fittest chromosome is: " + population.getNthFittest(0).getFitness());
//            System.out.println("Population fitness is: " + population.getPopulationFitness());

            long l = System.currentTimeMillis();
            population = ga.crossoverPopulation(population);
//            System.out.println("crossover: " + (System.currentTimeMillis() - l));
            l = System.currentTimeMillis();
            population = ga.mutatePopulation(population);
//            System.out.println("mutate: " + (System.currentTimeMillis() - l));

            ++generationCounter;
            if (generationCounter % 10 == 0) {
                System.out.println(generationCounter);
            }
//            System.out.println(generationCounter);

            gameStates = new ArrayList<>();
            l = System.currentTimeMillis();
            ga.evalPopulationForStates(population, gameStates);
//            System.out.println("eval: " + (System.currentTimeMillis() - l));
        }

//        System.out.println(String.format("Found solution in %d generation(s)", generationCounter));
//        System.out.println(String.format("Best solution is: %s", population.getNthFittest(0).toString()));


        return new Simulation(Evaluator.getField(), gameStates);
    }
}
