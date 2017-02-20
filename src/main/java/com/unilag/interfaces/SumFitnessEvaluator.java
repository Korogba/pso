package com.unilag.interfaces;

import org.moeaframework.core.FitnessEvaluator;

/**
 * Evaluates a population and assigns fitness values to its solutions.
 * Implemented by {@link com.unilag.fitness.WSumFitnessEvaluator}
 * @see FitnessEvaluator
 */
public interface SumFitnessEvaluator extends FitnessEvaluator {
    /**
     * Attribute key for the weighted sum fitness of a solution.
     */
    String WSUM_FITNESS_ATTRIBUTE = "wsum";
}
