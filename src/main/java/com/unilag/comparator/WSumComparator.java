package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.fitness.FitnessBasedArchive;

import java.io.Serializable;

/**
 * Created by @Kaba_Y on 22/01/2017
 * The Proposed PSO will keep a personal archive of non-dominated solutions
 * found by particle of the swarm
 *
 *  * References:
 * <ol>
 *   <li>Branke, Jürgen, and Sanaz Mostaghim.
 *   "About selecting the personal best in multi-objective particle swarm optimization."
 *   Parallel Problem Solving from Nature-PPSN IX. Springer Berlin Heidelberg, 2006. 523-532. APA
 *   </li>
 * </ol>
 *
 * Implements a DominanceComparator to be compatible with the MOEAFramework
 * This comparator uses the algorithm defined in the paper above to
 * select personal bests for each particle in the swarm
 *
 */
public class WSumComparator implements DominanceComparator, Serializable {

    private static final long serialVersionUID = -1779814832721413540L;

    private static String WSUM_FITNESS_VALUE = "WSUM";

    private FitnessBasedArchive population;
    private Solution particle;

    /**
     * Constructs a WSumComparator
     */
    public WSumComparator(FitnessBasedArchive population, Solution particle) {
        super();
        this.population = population;
        this.particle = particle;
        init();
    }

    /**
     * Compares the two solutions using a NWSum, returning
     * {@code -1} if {@code solutionOne} has a lesser weighted sum value than {@code solutionTwo},
     * {@code 1} if {@code solutionTwo} has a lesser weighted sum value {@code solutionOne},
     * and {@code 0} if the solutions have the same weighted sum value
     *
     * @param solutionOne the first solution
     * @param solutionTwo the second solution
     * @return {@code -1} if {@code solutionOne} has a lesser weighted sum value {@code solutionTwo},
     *         {@code 1} if {@code solutionTwo} has a lesser weighted sum value {@code solutionOne}, and
     *         {@code 0} if the solutions ave the same weighted sum value
     */
    @Override
    public int compare(Solution solutionOne, Solution solutionTwo) {
        if ((double)solutionOne.getAttribute(WSUM_FITNESS_VALUE) == (double) solutionTwo.getAttribute(WSUM_FITNESS_VALUE)) {
            return 0;
        } else if ((double) solutionOne.getAttribute(WSUM_FITNESS_VALUE) < (double) solutionTwo.getAttribute(WSUM_FITNESS_VALUE)) {
            return -1;
        } else {
            return 1;
        }
    }

    public Solution selectLeader() {
        Solution leader = population.get(0);
        for(Solution solution : population) {
            if(compare(solution, leader) == -1) {
                leader = solution;
            }
        }
        return leader;
    }

    /**
     * Assign weighted sum to each particle in the archive using
     */
    private void init() {
        for(Solution localBest : population) {
            double weightedAggregate = 0;
            for(int i = 0; i < localBest.getNumberOfObjectives(); i++) {
                double objectiveSum = 0;
                for(Solution innerBest: population) {
                    objectiveSum += innerBest.getObjective(i);
                }
                weightedAggregate += (localBest.getObjective(i)/objectiveSum)*particle.getObjective(i);
            }
            localBest.setAttribute(WSUM_FITNESS_VALUE, weightedAggregate);
        }
    }
}
