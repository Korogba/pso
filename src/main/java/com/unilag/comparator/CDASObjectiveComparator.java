package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

import java.io.Serializable;
import java.util.Comparator;

public class CDASObjectiveComparator implements DominanceComparator, Comparator<Solution>, Serializable {

    private static final long serialVersionUID = 7786775961720339760L;

    /**
     * Attribute key for the CDAS value of a solution.
     */
    public static final String CDAS_ATTRIBUTE = "cdas";

    CDASObjectiveComparator(Solution[] particles, double delta) {
        /**
         * STEP ONE
         * */
        int objectiveCount = particles[0].getNumberOfObjectives();
        double[] modifiedOrigin = new double[objectiveCount];
        for(int i = 0; i < objectiveCount; i++) {
            double minValue = particles[0].getObjective(i);
            for(Solution particle : particles) {
                if(particle.getObjective(i) < minValue) {
                    minValue = particle.getObjective(i);
                }
            }
            modifiedOrigin[i] = minValue - delta;
        }
        /**
         * STEP TWO
         * */
        double[][] landmarkVectors = new double[objectiveCount][objectiveCount];
        for(int i = 0; i < objectiveCount; i++) {
            double maxValue = particles[0].getObjective(i);
            for(Solution particle : particles) {
                if(particle.getObjective(i) > maxValue) {
                    maxValue = particle.getObjective(i);
                }
            }
            for(int j = 0; j < objectiveCount; j++){
                if(j != i) {
                    landmarkVectors[i][j] = modifiedOrigin[j];
                } else {
                    landmarkVectors[i][j] = maxValue - delta;
                }
            }
        }
        /**
         * STEP THREE
         * */
        for(Solution particle : particles) {
            double[] phiAngle = new double[objectiveCount];
            for(int i = 0; i < objectiveCount; i++) {
                double x = particle.getObjective(i);
                double lsum = 0;
                for(int j = 0; j < objectiveCount; j++) {
                    lsum += Math.pow(x - landmarkVectors[i][j], 2);
                }
                double distanceToLandmarkVector = Math.sqrt((lsum));
                double rsum = 0;
                for(int k = 0; k < modifiedOrigin.length; k++) {
                    rsum += Math.pow(x - modifiedOrigin[k], 2);
                }
                double distanceFromOrigin = Math.sqrt(rsum);
                double omegaAngle = Math.acos(x/distanceFromOrigin);
                phiAngle[i] = Math.asin((rsum * Math.sin(omegaAngle))/distanceToLandmarkVector);
            }
            for(Solution innerParticle : particles) {
                if(innerParticle != particle) {
                    for(int m = 0; m < objectiveCount; m++) {
                        double y = innerParticle.getObjective(m);
                        double innerRsum = 0;
                        for(int l = 0; l < modifiedOrigin.length; l++) {
                            innerRsum += Math.pow(y - modifiedOrigin[l], 2);
                        }
                        double innerDistanceFromOrigin = Math.sqrt(innerRsum);
                        double innerOmegaAngle = Math.acos(y/innerDistanceFromOrigin);
                        double innerCdas = (y * Math.sin(innerOmegaAngle + phiAngle[m]))/Math.sin(phiAngle[m]);
                        innerParticle.setAttribute(CDAS_ATTRIBUTE, innerCdas);
                    }
                }
            }
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
        return 0;
    }
}
