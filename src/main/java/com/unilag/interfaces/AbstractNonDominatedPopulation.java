package com.unilag.interfaces;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;

/**
 *
 */
public abstract class AbstractNonDominatedPopulation extends NondominatedPopulation {

    public AbstractNonDominatedPopulation(DominanceComparator comparator) {
        super(comparator);
    }

    public abstract void update();

    public abstract Solution getBest();
}
