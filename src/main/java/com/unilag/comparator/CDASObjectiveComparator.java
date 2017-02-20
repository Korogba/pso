package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Comparator;

public class CDASObjectiveComparator implements DominanceComparator, Comparator<Solution>, Serializable {

    private static final long serialVersionUID = 7572746976131192952L;

    private static final Logger log = LoggerFactory.getLogger(CDASObjectiveComparator.class);

    private double userDefinedParameter;

    public CDASObjectiveComparator (double userDefinedParameter) {
        if(userDefinedParameter <= 0 || userDefinedParameter >=1) {
            throw new IllegalArgumentException("Illegal arguments supplied");
        }
        this.userDefinedParameter = userDefinedParameter;
    }

    /**
     * Compares the two solutions using a CDAS dominance relation, returning
     * {@code -1} if {@code firstSolution} dominates {@code secondSolution}, {@code 1} if
     * {@code secondSolution} dominates {@code firstSolution}, and {@code 0} if the
     * solutions are non-dominated.
     *
     * @param firstSolution the first solution
     * @param secondSolution the second solution
     * @return {@code -1} if {@code firstSolution} dominates {@code secondSolution},
     *         {@code 1} if {@code secondSolution} dominates {@code firstSolution}, and
     *         {@code 0} if the solutions are non-dominated
     */
    @Override
    public int compare(Solution firstSolution, Solution secondSolution) {
        boolean dominateOne = false;
        boolean dominateTwo = false;

        double[] firstSolutionAttribute = getModifiedFitnessValue(firstSolution);
        double[] secondSolutionAttribute = getModifiedFitnessValue(secondSolution);

        for (int i = 0; i < firstSolution.getNumberOfObjectives(); i++) {
            if (firstSolutionAttribute[i] < secondSolutionAttribute[i]) {
                dominateOne = true;
                if (dominateTwo) {
                    return 0;
                }
            } else if (firstSolutionAttribute[i] > secondSolutionAttribute[i]) {
                dominateTwo = true;
                if (dominateOne) {
                    return 0;
                }
            }
        }

        if (dominateOne == dominateTwo) {
            return 0;
        } else if (dominateOne) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Compares the two solutions using a CDAS dominance relation, returning
     * {@code -1} if {@code firstSolution} dominates {@code secondSolution}, {@code 1} if
     * {@code secondSolution} dominates {@code firstSolution}, and {@code 0} if the
     * solutions are non-dominated.
     *
     * @param particle the first solution
     * @return {@link Double[]}, the modified fitness values for this particle with
     *                           {@link CDASObjectiveComparator#userDefinedParameter} parameter
     */
    private double[] getModifiedFitnessValue(Solution particle) {
        int objectiveCount = particle.getNumberOfObjectives();
        double vectorSum = 0;
        double[] cdasFitnessAttribute = new double[objectiveCount];
        for (int i = 0; i < objectiveCount; i++) {
            vectorSum += Math.pow(particle.getObjective(i), 2);
        }
        double vectorNorm = Math.sqrt(vectorSum);
        for(int j = 0; j < objectiveCount; j++) {
            double omegaAngle = Math.acos(particle.getObjective(j)/vectorNorm);
            double phiAngle = Math.PI * userDefinedParameter;
            cdasFitnessAttribute[j] =  (vectorNorm * Math.sin(phiAngle + omegaAngle))/Math.sin(phiAngle);
        }
        return  cdasFitnessAttribute;
    }
}
