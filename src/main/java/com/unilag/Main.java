package com.unilag;

import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class Main {
    public static void main(String[] args) throws Exception {

//        Analyzer analyzer = new Analyzer()
//                .withProblem("DTLZ2_10")
//                .includeAllMetrics()
//                .showStatisticalSignificance();
//
//        Executor executor = new Executor()
//                .withProblem("DTLZ2_10")
//                .withMaxEvaluations(100);
//
//
//        analyzer.addAll("NSGA-III",
//                executor.              withAlgorithm("NSGA-III").runSeeds(10));
//        analyzer.addAll("MOEAD",
//                executor.withAlgorithm("MOEAD").runSeeds(10));
//        analyzer.addAll("CSPSO",
//                executor.withAlgorithm("CSPSO").runSeeds(10));
//
//        analyzer.printAnalysis();

        NondominatedPopulation nsgaII = new Executor()
                    .withProblem("")
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(10000)
                .run();

        NondominatedPopulation moead = new Executor()
                .withProblem("DTLZ2_2")
                .withAlgorithm("MOEAD")
                .withMaxEvaluations(10000)
                .run();

        NondominatedPopulation cspso = new Executor()
                .withProblem("DTLZ2_2")
                .withAlgorithm("CSPSO")
                .withMaxEvaluations(10000)
                .run();

        Plot plot = new Plot();

        plot.add("NSGA-II", nsgaII)
            .add("MOEAD", moead)
            .add("CSPSO", cspso);

        plot.show();

        System.out.println("CSPSO: with archive size - " + cspso.size());
        for(Solution solution : cspso) {
            System.out.println(solution.getObjective(0) + " " + solution.getObjective(1));
        }

        System.out.println("=========================================================================================");

        System.out.println("NSGA: with archive size - " + nsgaII.size());
        for(Solution solution : nsgaII) {
            System.out.println(solution.getObjective(0) + " " + solution.getObjective(1));
        }

        System.out.println("=========================================================================================");

        System.out.println("MOEAD: with archive size - " + moead.size());
        for(Solution solution : nsgaII) {
            System.out.println(solution.getObjective(0) + " " + solution.getObjective(1));
        }
    }
}
