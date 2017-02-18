package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;

/**
 * Created by @Kaba_Y on 22/01/2017
 * "The controlling dominance area of solutions is a method to control the dominance area
 * of solutions in order to induce appropriate ranking of solutions for the problem
 * at hand, enhance selection, and improve the performance of MOEAs on
 * combinatorial optimization problems. It can control the degree
 * of expansion or contraction of the dominance area of
 * solutions using a user-defined parameter S."
 *
 *  * References:
 * <ol>
 *   <li>Sato, H., Aguirre, H. E., & Tanaka, K. (2007).
 *       Controlling Dominance Area of Solutions and Its Impact on the Performance of MOEAs.
 *       In H. Sato, H. E. Aguirre, & K. Tanaka, Evolutionary Multi-Criterion Optimization
 *       (pp. 5-20). Berlin Heidelberg: Springer.
 *   </li>
 * </ol>
 *
 * Implements a DominanceComparator to be compatible with the MOEAFramework
 * Extend the FitnessBasedArchive required by the AbstractPSO
 * FitnessBasedArchive extends NondominatedPopulation for which we will need to override the
 * Pareto dominance used to use the Dominance comparator used here
 * Extend ChainedDominance instead of directly implementing the DominanceComparator
 * This allows to incorporate the ConstraintViolation comparison
 *
 */
public class CDASComparator extends ChainedComparator {

    private static final long serialVersionUID = -7897049969086106070L;

    /**
     * The particles.
     */
    private Solution[] particles;

    /**
     * Small value in calculating the origin
     */
    private double delta;

    //ToDo: Remove; this is for tests only
    private CDASObjectiveComparator cdasObjectiveComparator;
    /**
     * Constructs a CDAS dominance comparator.
     * @param particles CDAS requires all the particles in the front
     */
    public CDASComparator(Solution[] particles, double delta) {
        super(new AggregateConstraintComparator(), new SCDASObjectiveComparator(particles, delta));
        this.cdasObjectiveComparator = new CDASObjectiveComparator(particles, delta);
        this.particles = particles;
        this.delta = delta;
    }

    public Solution[] getParticles() {
        return particles;
    }

    public void setParticles(Solution[] particles) {
        this.particles = particles;
    }

    public double getDelta() {
        return delta;
    }

    public CDASObjectiveComparator getCdasObjectiveComparator() {
        return cdasObjectiveComparator;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }
}
