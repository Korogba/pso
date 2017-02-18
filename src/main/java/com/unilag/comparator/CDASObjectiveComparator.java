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
    public static final String CDAS_FITNESS_ATTRIBUTE = "cdas";

    public CDASObjectiveComparator (Solution[] particles, double userDefinedParameter) {
        if(particles.length <= 0 || userDefinedParameter <= 0 || userDefinedParameter >=1) {
            throw new IllegalArgumentException("Illegal arguments supplied");
        }
        int objectiveCount = particles[0].getNumberOfObjectives();


        for(Solution particle : particles) {
            double vectorSum = 0;
            double[] cdasFitnessAttribute = new double[objectiveCount];
            for (int i = 0; i < objectiveCount; i++) {
                vectorSum += Math.pow(particle.getObjective(i), 2);
            }
            double vectorNorm = Math.sqrt(vectorSum);
            log.info("Vector Norm: " + vectorNorm);
            for(int j = 0; j < objectiveCount; j++) {
                double omegaAngle = Math.acos(particle.getObjective(j)/vectorNorm);
                log.info("Omega Angle: " + omegaAngle);
                double phiAngle = Math.PI * userDefinedParameter;
                log.info("Phi Angle: " + phiAngle);
                cdasFitnessAttribute[j] =  (vectorNorm * Math.sin(phiAngle + omegaAngle))/Math.sin(phiAngle);
            }
            particle.setAttribute(CDAS_FITNESS_ATTRIBUTE, cdasFitnessAttribute);
        }
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

        for (int i = 0; i < firstSolution.getNumberOfObjectives(); i++) {
            if ((Double) firstSolution.getAttribute(CDAS_FITNESS_ATTRIBUTE) < (Double) secondSolution.getAttribute(CDAS_FITNESS_ATTRIBUTE)) {
                dominateOne = true;
                if (dominateTwo) {
                    return 0;
                }
            } else if ((Double)firstSolution.getAttribute(CDAS_FITNESS_ATTRIBUTE) > (Double) secondSolution.getAttribute(CDAS_FITNESS_ATTRIBUTE)) {
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
}
