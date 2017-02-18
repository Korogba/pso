package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Comparator;

public class SCDASObjectiveComparator implements DominanceComparator, Comparator<Solution>, Serializable {

    private static final long serialVersionUID = 7786775961720339760L;

    private static final Logger log = LoggerFactory.getLogger(SCDASObjectiveComparator.class);


    /**
     * Attribute key for the CDAS value of a solution.
     */
    public static final String SCDAS_FITNESS_ATTRIBUTE = "scdas";

    private double[] modifiedOrigin;

    private double[][] landmarkVectors;

    private double[][] phiAngle;

    SCDASObjectiveComparator(Solution[] particles, double delta) {
        /**
         * STEP ONE
         * */
        int objectiveCount = particles[0].getNumberOfObjectives();
        modifiedOrigin = new double[objectiveCount];
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
        landmarkVectors = new double[objectiveCount][objectiveCount];
        for(int i = 0; i < objectiveCount; i++) {
            double maxValue = particles[0].getObjective(i);
            for(Solution particle : particles) {
                if(particle.getObjective(i) > maxValue) {
                    maxValue = particle.getObjective(i);
                }
            }
            for(int j = 0; j < objectiveCount; j++){
                if(j != i) {
                    landmarkVectors[i][j] = 0;
                } else {
                    landmarkVectors[i][j] = maxValue - delta;
                }
            }
        }
        /**
         * STEP THREE
         * */
        phiAngle = new double[particles.length][objectiveCount];
        for(int z = 0; z < particles.length; z++) {
            Solution particle = particles[z];
            for(int i = 0; i < objectiveCount; i++) {
                //log.info("Phi angle for Particle:Objective - " + z + ":" +i);
                double x = particle.getObjective(i);
                //log.info("Objective value: " +x);
                double lsum = 0;
                for(int j = 0; j < objectiveCount; j++) {
                    lsum += Math.pow(particle.getObjective(i) - landmarkVectors[i][j], 2);
                }
                double distanceToLandmarkVector = Math.sqrt(lsum);
                //log.info("Distance to landmark vector: " + distanceToLandmarkVector);
                double rsum = 0;
                for(int k = 0; k < objectiveCount; k++) {
                    rsum += Math.pow(particle.getObjective(k), 2);
                }
                double distanceFromOrigin = Math.sqrt(rsum);
                //log.info("Distance from origin: " + distanceFromOrigin);
                double omegaAngle = Math.toDegrees(Math.acos(x/distanceFromOrigin));
                //log.info("Omega angle: " + omegaAngle);
                double radianOmegaAngle = Math.toRadians(omegaAngle);
                double sineOmega = Math.sin(radianOmegaAngle);
                double radianPhi = Math.asin((distanceFromOrigin * sineOmega)/distanceToLandmarkVector);
                phiAngle[z][i] = Math.toDegrees(radianPhi);
                //log.info("Phi angle: " + phiAngle[z][i]);
            }
            for(Solution innerParticle : particles) {
                if(innerParticle != particle) {
                    double[] cdasFitnessAttribute = new double[objectiveCount];
                    double innerRsum = 0;
                    for(int l = 0; l < objectiveCount; l++) {
                        innerRsum += Math.pow(particle.getObjective(l), 2);
                    }
                    double innerDistanceFromOrigin = Math.sqrt(innerRsum);
                    for(int m = 0; m < objectiveCount; m++) {
                        double y = innerParticle.getObjective(m);
                        double innerOmegaAngle = Math.acos(y/innerDistanceFromOrigin);
                        double innerCdas;
                        if(Double.isNaN(innerOmegaAngle) || Double.isNaN(phiAngle[z][m]) || phiAngle[z][m] == 0) {
                            innerCdas = y;
                        } else {
                            innerCdas = (y * Math.sin(innerOmegaAngle + phiAngle[z][m])) / Math.sin(phiAngle[z][m]);
                        }
                        cdasFitnessAttribute[m] = innerCdas;
                    }
                    innerParticle.setAttribute(SCDAS_FITNESS_ATTRIBUTE, cdasFitnessAttribute);
                }
            }
        }

    }

    public double[] getModifiedOrigin() {
        return modifiedOrigin;
    }

    public void setModifiedOrigin(double[] modifiedOrigin) {
        this.modifiedOrigin = modifiedOrigin;
    }

    public double[][] getLandmarkVectors() {
        return landmarkVectors;
    }

    public void setLandmarkVectors(double[][] landmarkVectors) {
        this.landmarkVectors = landmarkVectors;
    }

    public double[][] getPhiAngle() {
        return phiAngle;
    }

    public void setPhiAngle(double[][] phiAngle) {
        this.phiAngle = phiAngle;
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
