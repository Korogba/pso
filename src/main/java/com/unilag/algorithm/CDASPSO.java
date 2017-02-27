package com.unilag.algorithm;

import com.unilag.comparator.CDASComparator;
import com.unilag.fitness.ShiftBasedDensityEvaluator;
import org.moeaframework.algorithm.pso.AbstractPSOAlgorithm;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.fitness.CrowdingDistanceFitnessEvaluator;
import org.moeaframework.core.fitness.FitnessBasedArchive;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

/**
 * Extension of {@link org.moeaframework.algorithm.pso.SMPSO}, the speed-constrained multi-objective particle
 * swarm optimizer.
 * Uses on CDAS
 * For test purposes
 */
public class CDASPSO extends AbstractPSOAlgorithm {

    /**
     * The minimum velocity for each variable.
     */
    private double[] minimumVelocity;

    /**
     * The maximum velocity for each variable.
     */
    private double[] maximumVelocity;

    public CDASPSO(Problem problem, int swarmSize, int leaderSize, double mutationProbability, double distributionIndex, double userDefinedParameter) {
        super(problem, swarmSize, leaderSize, new CrowdingComparator(),
                new ParetoDominanceComparator(),
                new FitnessBasedArchive(new ShiftBasedDensityEvaluator(), leaderSize, new CDASComparator(userDefinedParameter)),
                null,
                new PM(mutationProbability, distributionIndex));

        // initialize the minimum and maximum velocities
        minimumVelocity = new double[problem.getNumberOfVariables()];
        maximumVelocity = new double[problem.getNumberOfVariables()];

        Solution prototypeSolution = problem.newSolution();

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            RealVariable variable = (RealVariable)prototypeSolution.getVariable(i);
            maximumVelocity[i] = (variable.getUpperBound() - variable.getLowerBound()) / 2.0;
            minimumVelocity[i] = -maximumVelocity[i];
        }
    }

    @Override
    protected void updateVelocity(int i) {
        Solution particle = particles[i];
        Solution localBestParticle = localBestParticles[i];
        Solution leader = selectLeader();

        double r1 = PRNG.nextDouble();
        double r2 = PRNG.nextDouble();
        double C1 = PRNG.nextDouble(1.5, 2.5);
        double C2 = PRNG.nextDouble(1.5, 2.5);
        double W = PRNG.nextDouble(0.1, 0.1);

        for (int j = 0; j < problem.getNumberOfVariables(); j++) {
            double particleValue = EncodingUtils.getReal(particle.getVariable(j));
            double localBestValue = EncodingUtils.getReal(localBestParticle.getVariable(j));
            double leaderValue = EncodingUtils.getReal(leader.getVariable(j));

            double velocity = constrictionCoefficient(C1, C2) *
                    (W * velocities[i][j] +
                            C1*r1*(localBestValue - particleValue) +
                            C2*r2*(leaderValue - particleValue));

            if (velocity > maximumVelocity[j]) {
                velocity = maximumVelocity[j];
            } else if (velocity < minimumVelocity[j]) {
                velocity = minimumVelocity[j];
            }

            velocities[i][j] = velocity;
        }
    }

    /**
     * Returns the velocity constriction coefficient.
     *
     * @param c1 the velocity coefficient for the local best
     * @param c2 the velocity coefficient for the leader
     * @return the velocity constriction coefficient
     */
    private double constrictionCoefficient(double c1, double c2) {
        double rho = c1 + c2;

        if (rho <= 4) {
            return 1.0;
        } else {
            return 2.0 / (2.0 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
        }
    }

    @Override
    protected void mutate(int i) {
        // The SMPSO paper [1] states that mutation is applied 15% of the time,
        // but the JMetal implementation applies to every 6th particle.  Should
        // the application of mutation be random instead?
        if (i % 6 == 0) {
            particles[i] = mutation.evolve(new Solution[] { particles[i] })[0];
        }
    }
}
