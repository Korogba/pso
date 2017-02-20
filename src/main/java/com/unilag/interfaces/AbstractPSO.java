package com.unilag.interfaces;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.algorithm.AlgorithmInitializationException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractPSO extends AbstractAlgorithm {
    /**
     * The original implementation of OMOPSO in JMetal returns the leaders
     * instead of the epsilon-dominance archive as described in the literature.
     * This results in a small performance difference that is detected by our
     * unit tests.  To enable unit testing to compare the two implementations,
     * this flag forces OMOPSO to behave like the JMetal implementation and
     * only return the leaders.
     */
    private static boolean TESTING_MODE = false;

    /**
     * The number of particles.
     */
    protected int swarmSize;

    /**
     * The number of leaders.
     */
    protected int leaderSize;

    /**
     * The particles.
     */
    protected Solution[] particles;

    /**
     * The local best particles.
     */
    protected HashMap<Integer, AbstractNonDominatedPopulation> localBestParticles;

    /**
     * The leaders.
     */
    protected AbstractNonDominatedPopulation leaders;

    /**
     * The archive of non-dominated solutions; or {@code null} of no external
     * archive is sued.
     */
    protected NondominatedPopulation archive;

    /**
     * The speed / velocity of each particle.
     */
    protected double[][] velocities;

    /**
     * Comparator for selecting leaders.
     */
    protected DominanceComparator leaderComparator;

    /**
     * Comparator for updating the local best particles.
     */
    protected DominanceComparator dominanceComparator;

    /**
     * Mutation operator, or {@code null} if no mutation is defined.
     */
    protected Variation mutation;

    /**
     * Constructs a new abstract PSO algorithm.
     *
     * @param problem the problem
     * @param swarmSize the number of particles
     * @param leaderSize the number of leaders
     * @param leaderComparator comparator for selecting leaders
     * @param dominanceComparator comparator for updating the local best
     *        particles
     * @param leaders non-dominated population for storing the leaders
     * @param archive non-dominated population for storing the external archive;
     *        or {@code null} if no external archive is defined
     * @param mutation mutation operator, or {@code null} if no mutation is
     *        defined
     */
    public AbstractPSO(Problem problem, int swarmSize, int leaderSize,
                                DominanceComparator leaderComparator,
                                DominanceComparator dominanceComparator,
                                AbstractNonDominatedPopulation leaders,
                                NondominatedPopulation archive,
                                Variation mutation) {
        super(problem);
        this.swarmSize = swarmSize;
        this.leaderSize = leaderSize;
        this.leaderComparator = leaderComparator;
        this.dominanceComparator = dominanceComparator;
        this.leaders = leaders;
        this.archive = archive;
        this.mutation = mutation;

        particles = new Solution[swarmSize];
        localBestParticles =  new HashMap<>(swarmSize);
        velocities = new double[swarmSize][problem.getNumberOfVariables()];
    }

    public abstract void initializePersonalBestArchive(int index);

    /**
     * Update the speeds of all particles.
     */
    protected void updateVelocities() {
        for (int i = 0; i < swarmSize; i++) {
            updateVelocity(i);
        }
    }

    /**
     * Update the speed of an individual particle.
     *
     * @param i the index of the particle
     */
    protected void updateVelocity(int i) {
        Solution particle = particles[i];
        Solution localBestParticle = getLocalBestParticle(i);
        Solution leader = selectLeader(i);

        double r1 = PRNG.nextDouble();
        double r2 = PRNG.nextDouble();
        double C1 = PRNG.nextDouble(1.5, 2.0);
        double C2 = PRNG.nextDouble(1.5, 2.0);
        double W = PRNG.nextDouble(0.1, 0.5);

        for (int j = 0; j < problem.getNumberOfVariables(); j++) {
            double particleValue = EncodingUtils.getReal(particle.getVariable(j));
            double localBestValue = EncodingUtils.getReal(localBestParticle.getVariable(j));
            double leaderValue = EncodingUtils.getReal(leader.getVariable(j));

            velocities[i][j] = W * velocities[i][j] +
                    C1*r1*(localBestValue - particleValue) +
                    C2*r2*(leaderValue - particleValue);
        }
    }

    /**
     * Update the positions of all particles.
     */
    protected void updatePositions() {
        for (int i = 0; i < swarmSize; i++) {
            updatePosition(i);
        }
    }

    /**
     * Update the position of an individual particle.
     *
     * @param i the index of the particle
     */
    protected void updatePosition(int i) {
        Solution parent = particles[i];
        Solution offspring = parent.copy();

        for (int j = 0; j < problem.getNumberOfVariables(); j++) {
            RealVariable variable = (RealVariable)offspring.getVariable(j);
            double value = variable.getValue() + velocities[i][j];

            if (value < variable.getLowerBound()) {
                value = variable.getLowerBound();
                velocities[i][j] *= -1;
            } else if (value > variable.getUpperBound()) {
                value = variable.getUpperBound();
                velocities[i][j] *= -1;
            }

            variable.setValue(value);
        }

        particles[i] = offspring;
    }

    /**
     * Randomly select a leader.
     *
     * @return the selected leader
     */
    protected Solution selectLeader(int index) {
        Solution leader1 = leaders.get(PRNG.nextInt(leaders.size()));
        Solution leader2 = leaders.get(PRNG.nextInt(leaders.size()));
        int flag = leaderComparator.compare(leader1, leader2);

        if (flag < 0) {
            return leader1;
        } else if (flag > 0) {
            return leader2;
        } else if (PRNG.nextBoolean()) {
            return leader1;
        } else {
            return leader2;
        }
    }

    public Solution getLocalBestParticle(int index) {
        return localBestParticles.get(index).getBest();
    }

    /**
     * Updates the local best particles.
     */
    protected void updateLocalBest() {
        for (int i = 0; i < swarmSize; i++) {
            localBestParticles.get(i).add(particles[i]);
        }
    }

    /**
     * Applies the mutation operator to all particles.
     */
    protected void mutate() {
        for (int i = 0; i < swarmSize; i++) {
            mutate(i);
        }
    }

    /**
     * Applies the mutation operator to an individual particle.
     *
     * @param i the index of the particle
     */
    protected void mutate(int i) {
        if (mutation != null) {
            particles[i] = mutation.evolve(new Solution[] { particles[i] })[0];
        }
    }

    @Override
    public NondominatedPopulation getResult() {
        if (archive == null || TESTING_MODE) {
            return new NondominatedPopulation(leaders);
        } else {
            return new NondominatedPopulation(archive);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();

        Solution[] initialParticles = new RandomInitialization(problem,
                swarmSize).initialize();

        evaluateAll(initialParticles);

        for (int i = 0; i < swarmSize; i++) {
            particles[i] = initialParticles[i];
            initializePersonalBestArchive(i);
            localBestParticles.get(i).add(initialParticles[i]);
        }

        leaders.addAll(initialParticles);
        leaders.update();

        if (archive != null) {
            archive.addAll(initialParticles);
        }
    }

    @Override
    protected void iterate() {
        updateVelocities();
        updatePositions();
        mutate();

        evaluateAll(particles);

        updateLocalBest();
        leaders.addAll(particles);
        leaders.update();

        if (archive != null) {
            archive.addAll(particles);
        }
    }

    @Override
    public Serializable getState() throws NotSerializableException {
        if (!isInitialized()) {
            throw new AlgorithmInitializationException(this,
                    "algorithm not initialized");
        }

        List<Solution> particlesList = Arrays.asList(particles);
        HashMap<Integer, AbstractNonDominatedPopulation> localBestParticlesList = localBestParticles;
        List<Solution> leadersList = new ArrayList<Solution>();
        List<Solution> archiveList = new ArrayList<Solution>();
        double[][] velocitiesClone = new double[velocities.length][];

        for (Solution solution : leaders) {
            leadersList.add(solution);
        }

        if (archive != null) {
            for (Solution solution : archive) {
                archiveList.add(solution);
            }
        }

        for (int i = 0; i < velocities.length; i++) {
            velocitiesClone[i] = velocities[i].clone();
        }

        return new AbstractPSO.PSOAlgorithmState(getNumberOfEvaluations(),
                particlesList, localBestParticlesList, leadersList,
                archiveList, velocitiesClone);
    }

    @Override
    public void setState(Object objState) throws NotSerializableException {
        super.initialize();

        AbstractPSO.PSOAlgorithmState state = (AbstractPSO.PSOAlgorithmState)objState;

        numberOfEvaluations = state.getNumberOfEvaluations();

        if (state.getParticles().size() != swarmSize) {
            throw new NotSerializableException(
                    "swarmSize does not match serialized state");
        }

        for (int i = 0; i < swarmSize; i++) {
            particles[i] = state.getParticles().get(i);
        }

        for (int i = 0; i < swarmSize; i++) {
            localBestParticles = state.getLocalBestParticles();
        }

        leaders.addAll(state.getLeaders());
        leaders.update();

        if (archive != null) {
            archive.addAll(state.getArchive());
        }

        for (int i = 0; i < swarmSize; i++) {
            for (int j = 0; j < problem.getNumberOfVariables(); j++) {
                velocities[i][j] = state.getVelocities()[i][j];
            }
        }
    }

    /**
     * Proxy for serializing and deserializing the state of an
     * {@code AbstractPSOAlgorithm}. This proxy supports saving
     * the {@code numberOfEvaluations}, {@code population} and {@code archive}.
     */
    private static class PSOAlgorithmState implements Serializable {

        private static final long serialVersionUID = -1895823731827106938L;

        /**
         * The number of objective function evaluations.
         */
        private final int numberOfEvaluations;

        /**
         * The particles stored in a serializable list.
         */
        private final List<Solution> particles;

        /**
         * The local best particles stored in a serializable list.
         */
        private final HashMap<Integer, AbstractNonDominatedPopulation> localBestParticles;

        /**
         * The leaders stored in a serializable list.
         */
        private final List<Solution> leaders;

        /**
         * The archive stored in a serializable list.
         */
        private final List<Solution> archive;

        /**
         * The velocities.
         */
        private final double[][] velocities;

        /**
         * Constructs a proxy to serialize and deserialize the state of an
         * {@code AbstractPSOAlgorithm}.
         *
         * @param numberOfEvaluations the number of objective function
         *        evaluations
         * @param particles the population stored in a serializable list
         * @param archive the archive stored in a serializable list
         */
        public PSOAlgorithmState(int numberOfEvaluations,
                                 List<Solution> particles,
                                 HashMap<Integer, AbstractNonDominatedPopulation> localBestParticles,
                                 List<Solution> leaders,
                                 List<Solution> archive,
                                 double[][] velocities) {
            super();
            this.numberOfEvaluations = numberOfEvaluations;
            this.particles = particles;
            this.localBestParticles = localBestParticles;
            this.leaders = leaders;
            this.archive = archive;
            this.velocities = velocities;
        }

        /**
         * Returns the number of objective function evaluations.
         *
         * @return the number of objective function evaluations
         */
        public int getNumberOfEvaluations() {
            return numberOfEvaluations;
        }

        /**
         * Returns the particles stored in a serializable list.
         *
         * @return the particles stored in a serializable list
         */
        public List<Solution> getParticles() {
            return particles;
        }

        /**
         * Returns the local best particles stored in a serializable list.
         *
         * @return the local best particles stored in a serializable list
         */
        public HashMap<Integer, AbstractNonDominatedPopulation> getLocalBestParticles() {
            return localBestParticles;
        }

        /**
         * Returns the leaders stored in a serializable list.
         *
         * @return the leaders stored in a serializable list
         */
        public List<Solution> getLeaders() {
            return leaders;
        }

        /**
         * Returns the velocities.
         *
         * @return the velocities
         */
        public double[][] getVelocities() {
            return velocities;
        }

        /**
         * Returns the archive stored in a serializable list.
         *
         * @return the archive stored in a serializable list
         */
        public List<Solution> getArchive() {
            return archive;
        }

    }
}
