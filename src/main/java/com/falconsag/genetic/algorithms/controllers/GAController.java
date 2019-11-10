package com.falconsag.genetic.algorithms.controllers;

import com.falconsag.genetic.algorithms.RobotEvaluators;
import com.falconsag.genetic.algorithms.model.GeneticConfiguration;
import com.falconsag.genetic.algorithms.model.robot.GameSimulation;
import com.falconsag.genetic.algorithms.model.robot.Simulation;
import com.falconsag.genetic.algorithms.robot.Evaluator;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.EliteSelector;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import io.jenetics.engine.EvolutionStatistics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import static java.util.concurrent.ForkJoinPool.commonPool;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GAController {
    private static final int GENE_LENGTH = (int) Math.pow(3, 12);

    @GetMapping("/")
    public String hi() {
        return "welcome";
    }

    private List<Double> fitnessValues;


    public Integer count(Genotype<BitGene> gt) {
        return gt.getChromosome().as(BitChromosome.class).bitCount();
    }


    @PostMapping(value = "/evolve", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Simulation postForGameStates(@RequestBody GeneticConfiguration config) {

        ConcurrentLinkedDeque<GameSimulation> simulations = new ConcurrentLinkedDeque<>();

        io.jenetics.engine.Evaluator<BitGene, Double> reEvaluator = RobotEvaluators.reevaluateEvaluator(i -> new Evaluator(config, i.getChromosome().as(BitChromosome.class))
                .evaluate(simulations), commonPool());

        io.jenetics.engine.Evaluator<BitGene, Double> mainThreadEvaluator = population -> population.map(pt -> pt.eval(evaluateRobotChromosome(config, simulations))).asISeq();

        Engine.Builder customBuilder = new Engine.Builder(


                mainThreadEvaluator,

                Genotype.of(BitChromosome.of(4096, 0.5)));

        EvolutionStatistics<Integer, ?> integerDoubleMomentStatisticsEvolutionStatistics = EvolutionStatistics.ofNumber();

        Engine<BitGene, Double> engine = customBuilder
                .maximalPhenotypeAge(10000)
                .populationSize(100)
                .offspringFraction(0.1)
                .selector(new EliteSelector<>(1))
//                .selector(new RouletteWheelSelector<>())
                .alterers(new Mutator<>(0.1),
                          new SinglePointCrossover<>(0.05))
                .build();
//
        EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        fitnessValues = new ArrayList<>();
        Phenotype<BitGene, Double> collect = engine.stream()
//                                                   .limit(Limits.byExecutionTime(Duration.of(2, ChronoUnit.SECONDS)))
                                                   .limit(config.getSimulatorConfig().getGenLimit())
                                                   .peek(statistics)
                                                   .peek(this::consume)
                                                   .collect(toBestPhenotype());
        System.out.println(collect);
        System.out.println(statistics);
        GameSimulation gameSimulation = simulations.stream().sorted(Comparator.comparing(GameSimulation::getFitness, Comparator.reverseOrder())).findFirst().get();

        System.out.println("evaluated no: " + Evaluator.counter);
        return new Simulation(config.getEditorConfig().getMapArr(), gameSimulation.getGameStates(),
                              fitnessValues, config.getSimulatorConfig().getFoodMax(),
                              config.getSimulatorConfig().getFoodDecrease());


//        Evaluator.setMoveCost(moveCost);
//        Evaluator.setTurnCost(turnCost);
//        Evaluator.setDoNothingCost(doNothingCost);
//        Evaluator.setNumberOfSimulationSteps(numberOfSimulateSteps);
//        Evaluator.setFoodMin(foodMin);
//        Evaluator.setFoodMax(foodMax);
//        Evaluator.setFoodDecrease(foodDecrease);
//        Evaluator.init(editorConfig.getMapArr().stream().mapToInt(Integer::intValue).toArray(),
//                       new Coord(editorConfig.getRobot().getX(), editorConfig.getRobot().getY()),
//                       new Coord(editorConfig.getRobot().getDirX(), editorConfig.getRobot().getDirY()),
//                       editorConfig.getMazeSize()
//        );

//
//        List<Double> fitnessValues = new ArrayList<>();
//        RobotGA ga = new RobotGA(100, 0.01, 0.99, 2);
//        Population population = ga.initPopulation(GENE_LENGTH);
//        List<GameState> gameStates = new ArrayList<>();
//        ga.evalPopulationForStates(population, gameStates, numberOfSimulationsToAvg);
//        int generationCounter = 1;
//
//        long sumEval = 0;
//        long sumMutate = 0;
//        long sumCrossover = 0;
//
//        long prevT = getCurrentMillis();
//        while (!ga.isTerminationConditionMet(population) && generationCounter < genLimit) {
//            Chromosome nthFittest = population.getNthFittest(0);
//
//            Chromosome fittest = population.getNthFittest(0);
//            System.out.println("Fittest chromosome is: " + fittest.getFitness());
//            fitnessValues.add(fittest.getFitness());
////            System.out.println("Population fitness is: " + population.getPopulationFitness());
//
//            long l = getCurrentMillis();
//            population = ga.crossoverPopulation(population);
//            sumCrossover += (getCurrentMillis() - l);
//
//            l = getCurrentMillis();
//            population = ga.mutatePopulation(population);
//            sumMutate += (getCurrentMillis() - l);
//
//            ++generationCounter;
//            if (generationCounter % 10 == 0) {
//                long now = getCurrentMillis();
//                long procMs = now - prevT;
//                OffsetDateTime nowOD = OffsetDateTime.now();
//                System.out.println("" + generationCounter + " :" + procMs + "ms");
//
//                int rem = (genLimit - generationCounter) / 10;
//                double remSecs = (rem * procMs) / (double) 1000;
//                System.out.println("estimated remaining time: " + nowOD.plusSeconds((int) remSecs));
//                prevT = now;
//            }
////            System.out.println(generationCounter);
//
//            gameStates = new ArrayList<>();
//            l = getCurrentMillis();
//            ga.evalPopulationForStates(population, gameStates, numberOfSimulationsToAvg);
//            sumEval += (getCurrentMillis() - l);
//
//        }
//
//        System.out.println(sumEval);
//        System.out.println(sumCrossover);
//        System.out.println(sumMutate);
////        System.out.println(String.format("Found solution in %d generation(s)", generationCounter));
////        System.out.println(String.format("Best solution is: %s", population.getNthFittest(0).toString()));
//
//
//        return new Simulation(Evaluator.getField(), gameStates, fitnessValues);

    }

    private Function<Genotype<BitGene>, Double> evaluateRobotChromosome(@RequestBody GeneticConfiguration config, ConcurrentLinkedDeque<GameSimulation> simulations) {
        return i -> {

            return IntStream.rangeClosed(0, 5).mapToDouble(j -> new Evaluator(config, i.getChromosome().as(BitChromosome.class))
                    .evaluate(simulations)).average().getAsDouble();

//            return new Evaluator(config, i.getChromosome().as(BitChromosome.class))
//                    .evaluate(simulations);
        };
    }


    private void observer(EvolutionResult<BitGene, Integer> evRes) {

        Phenotype<BitGene, Integer> bitGeneIntegerPhenotype1 = evRes.getPopulation().stream().sorted(Comparator.comparing(bitGeneIntegerPhenotype -> bitGeneIntegerPhenotype.getFitness(), Comparator.reverseOrder())).findFirst().get();
        System.out.println("" + evRes.getGeneration() + " best: " + bitGeneIntegerPhenotype1);
        System.out.println(evRes);
    }


    private void consume(EvolutionResult<BitGene, Double> res) {
        System.out.println(res.getGeneration());
        fitnessValues.add(res.getPopulation().stream().mapToDouble(i -> i.getFitness()).max().getAsDouble())
        ;
    }

    private long getCurrentMillis() {
        return System.currentTimeMillis();
    }
}
