package com.unilag;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 * Main method
 */
public class Main {
    public static void main(String[] args){

        Analyzer analyzer = new Analyzer()
                .withProblem("DTLZ2_10")
                .includeAllMetrics()
                .showStatisticalSignificance();

        Executor executor = new Executor()
                .withProblem("DTLZ2_10")
                .withMaxEvaluations(10000);

        analyzer.addAll("CSPSO", executor.withAlgorithm("CSPSO").runSeeds(50));

        analyzer.addAll("SMPSO", executor.withAlgorithm("SMPSO").runSeeds(50));

        analyzer.printAnalysis();

        NondominatedPopulation cspso = new Executor()
                .withProblem("UF1")
                .withAlgorithm("CSPSO")
                .withMaxEvaluations(10000)
                .run();
        NondominatedPopulation smpso = new Executor()
                .withProblem("UF1")
                .withAlgorithm("SMPSO")
                .withMaxEvaluations(10000)
                .run();
        Plot plot = new Plot();
        plot.add("CSPSO", cspso);
        plot.add("SMPSO", smpso);
        plot.show();

        int k = 1;
        for (Solution solution : smpso) {
            System.out.println(k+": ");
            for(int i = 0; i < solution.getNumberOfObjectives(); i++) {
                System.out.print(solution.getObjective(i) + " ");
            }
            System.out.print("\nEnd of " + k + "\n");
            k++;
        }

        System.out.println("\n===================================================================================================");

        int j = 1;
        for (Solution solution : cspso) {
            System.out.println(j+": ");
            for(int i = 0; i < solution.getNumberOfObjectives(); i++) {
                System.out.print(solution.getObjective(i) + " ");
            }
            System.out.print("\nEnd of " + j + "\n");
            j++;
        }
    }
}
