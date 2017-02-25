package com.unilag.algorithm;

import com.unilag.comparator.CDASComparator;
import com.unilag.comparator.WSumComparator;
import com.unilag.fitness.DoubleComparatorArchive;
import com.unilag.fitness.ShiftBasedDensityEvaluator;
import com.unilag.fitness.WSumFitnessEvaluator;
import com.unilag.interfaces.AbstractPSO;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extension of {@link org.moeaframework.algorithm.pso.SMPSO}, the speed-constrained multi-objective particle
 * swarm optimizer.
 *
 * Uses a {@link com.unilag.fitness.DoubleComparatorArchive} as the archive for the leaders,
 * {@link com.unilag.comparator.CDASComparator} for the primary classifier,
 * {@link com.unilag.fitness.ShiftBasedDensityEvaluator} as a secondary classifier and
 * {@link com.unilag.fitness.WSumFitnessEvaluator} for local leadership selection
 *
 * <p>
 * References:
 * <ol>
 *   <li>Nebro, A. J., J. J. Durillo, J. Garcia-Nieto, and C. A. Coello Coello
 *       (2009).  SMPSO: A New PSO-based Metaheuristic for Multi-objective
 *       Optimization.  2009 IEEE Symposium on Computational Intelligence in
 *       Multi-Criteria Decision-Making, pp. 66-73.
 *   <li>Durillo, J. J., J. Garcia-Nieto, A. J. Nebro, C. A. Coello Coello,
 *       F. Luna, and E. Alba (2009).  Multi-Objective Particle Swarm
 *       Optimizers: An Experimental Comparison.  Evolutionary Multi-Criterion
 *       Optimization, pp. 495-509.
 * </ol>
 *
 * @see org.moeaframework.algorithm.pso.SMPSO
 *
 */
public class CSPSO extends AbstractPSO {

    /**
     * The minimum velocity for each variable.
     */
    private double[] minimumVelocity;

    /**
     * The maximum velocity for each variable.
     */
    private double[] maximumVelocity;

    /**
     * @param problem the multi-objective problem to be optimized
     * @param swarmSize the size of the swarm
     * @param leaderSize the size of the leader's archive
     * @param mutationProbability the probability this operator is applied to each solution particle
     * @param distributionIndex The distribution index controls the shape of the offspring distribution.
     *                          Larger values for the distribution index generates offspring closer to the parents.
     *
     * Uses a Polynomial mutation (PM) operator:
     *     Deb, Kalyanmoy, and Mayank Goyal.
     *     "A combined genetic adaptive search (GeneAS) for engineering design."
     *     Computer Science and informatics 26 (1996): 30-45.
     *
     * @see PM
     */
    public CSPSO(Problem problem, int swarmSize, int leaderSize, double mutationProbability, double distributionIndex, double userDefinedParameter) {
        super(problem, swarmSize, leaderSize, new WSumComparator(true),
                new WSumComparator(false),
                new DoubleComparatorArchive(new ShiftBasedDensityEvaluator(),
                        leaderSize, new CDASComparator(userDefinedParameter), new FitnessComparator(true)),
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
    public void initializePersonalBestArchive(int index) {
        DoubleComparatorArchive doubleComparatorArchive = new DoubleComparatorArchive(new WSumFitnessEvaluator(particles[index]), 5);
        localBestParticles.put(index, doubleComparatorArchive);
    }

    @Override
    protected void updateVelocity(int i) {
        Solution particle = particles[i];
        Solution localBestParticle = localBestParticles.get(i).getBest();
        Solution leader = selectLeader(i);

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

    /**
     * Randomly select a leader.
     *
     * @param index the particle whose leader is being selected
     * @return the selected leader
     */
    @Override
    protected Solution selectLeader(int index) {
        Solution leaderOne = leaders.get(PRNG.nextInt(leaders.size()));
        Solution leaderTwo = leaders.get(PRNG.nextInt(leaders.size()));
        Solution leaderThree = leaders.get(PRNG.nextInt(leaders.size()));

        Solution[] solutions = {leaderOne, leaderTwo, leaderThree};
        Population population = new Population(solutions);

        WSumFitnessEvaluator wSumFitnessEvaluator = new WSumFitnessEvaluator(particles[index]);
        wSumFitnessEvaluator.evaluate(population);

        List<Solution> leadersList = new ArrayList<>(Arrays.asList(solutions));
        leadersList.sort(leaderComparator::compare);

        return leadersList.get(0);
    }

    /**
     * Updates the local best particles.
     */
    @Override
    protected void updateLocalBest() {
        super.updateLocalBest();
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    public void evaluateAll(Solution[] solutions) {
        leaders.update();
        super.evaluateAll(solutions);
    }
}
