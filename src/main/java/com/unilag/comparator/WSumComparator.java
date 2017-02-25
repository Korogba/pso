package com.unilag.comparator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

import java.io.Serializable;
import java.util.Comparator;

import static com.unilag.interfaces.SumFitnessEvaluator.WSUM_FITNESS_ATTRIBUTE;

/**
 * Compares two solutions based on their {@code WSUM_FITNESS_ATTRIBUTE} value.
 * Follows the model of {@link org.moeaframework.core.comparator.FitnessComparator}
 *
 * @see com.unilag.fitness.WSumFitnessEvaluator
 */
public class WSumComparator implements DominanceComparator, Comparator<Solution>, Serializable {

    private static final long serialVersionUID = -4071122793199952834L;

    private boolean largerValuesPreferred;


    /**
     * Constructs a dominance comparator for comparing solutions based on their
     * {@code WSUM_FITNESS_ATTRIBUTE} value.
     *
     * @param largerValuesPreferred {@code true} if larger fitness values are
     *        preferred; otherwise smaller fitness values are preferred
     */
    public WSumComparator(boolean largerValuesPreferred) {
        super();
        this.largerValuesPreferred = largerValuesPreferred;
    }

    @Override
    public int compare(Solution firstSolution, Solution secondSolution) {
        return (largerValuesPreferred ? -1 : 1) * Double.compare(
                (Double)firstSolution.getAttribute(WSUM_FITNESS_ATTRIBUTE),
                (Double)secondSolution.getAttribute(WSUM_FITNESS_ATTRIBUTE));
    }
}
