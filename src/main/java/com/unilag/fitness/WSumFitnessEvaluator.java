package com.unilag.fitness;

import com.unilag.interfaces.SumFitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

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
 * Implements a FitnessEvaluator as Solutions will be expected to maintain an archive of personal bests.
 * This comparator uses the algorithm defined in the paper above to
 * select personal bests for each particle in the swarm
 *
 */
public class WSumFitnessEvaluator implements SumFitnessEvaluator, Serializable {

    private static final long serialVersionUID = -1779814832721413540L;

    private Solution particle;

    /**
     * Constructs a WSumFitnessEvaluator
     */
    public WSumFitnessEvaluator(Solution particle) {
        super();
        this.particle = particle;
    }

    @Override
    public void evaluate(Population population) {
        for(Solution individual : population) {
            double weightedAggregate = 0;
            double objectiveSum = 0;
            for(int i= 0; i < individual.getNumberOfObjectives(); i++) {
                objectiveSum += individual.getObjective(i);
            }
            for(int j = 0; j < individual.getNumberOfObjectives(); j++) {
                weightedAggregate += (individual.getObjective(j)/objectiveSum) * particle.getObjective(j);
            }
            individual.setAttribute(WSUM_FITNESS_ATTRIBUTE, weightedAggregate);
        }
    }

    @Override
    public boolean areLargerValuesPreferred() {
        return false;
    }
}
