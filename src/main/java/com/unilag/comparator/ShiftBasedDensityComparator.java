package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

import java.io.Serializable;
import java.util.Comparator;

import static org.moeaframework.core.FitnessEvaluator.FITNESS_ATTRIBUTE;

/**
 * Compares two solutions based on their {@code FITNESS_ATTRIBUTE} value.
 * Follows the model of {@link org.moeaframework.core.comparator.FitnessComparator}
 *
 * @see com.unilag.fitness.ShiftBasedDensityEvaluator
 */
public class ShiftBasedDensityComparator implements DominanceComparator, Comparator<Solution>, Serializable {

    private static final long serialVersionUID = -6289229153336051876L;

    private boolean largerValuesPreferred;

    /**
     * Constructs a dominance comparator for comparing solutions based on their
     * {@code FITNESS_ATTRIBUTE} value.
     *
     * @param largerValuesPreferred {@code true} if larger fitness values are
     *        preferred; otherwise smaller fitness values are preferred
     */
    public ShiftBasedDensityComparator(boolean largerValuesPreferred) {
        super();
        this.largerValuesPreferred = largerValuesPreferred;
    }

    @Override
    public int compare(Solution firstSolution, Solution secondSolution) {
        return (largerValuesPreferred ? -1 : 1) * Double.compare(
                (Double)firstSolution.getAttribute(FITNESS_ATTRIBUTE),
                (Double)secondSolution.getAttribute(FITNESS_ATTRIBUTE));
    }
}
