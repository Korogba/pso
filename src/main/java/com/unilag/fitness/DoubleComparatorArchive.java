package com.unilag.fitness;

import com.unilag.comparator.WSumComparator;
import com.unilag.interfaces.AbstractNonDominatedPopulation;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

import java.util.Comparator;

/**
  * Maintains a non-dominated archive of solutions with a maximum capacity.
  * Solutions are added only if they are non-dominated wrt to each other
  * If the size exceeds the capacity, one or more solutions are pruned based on
  * a secondary fitness calculation. The fitness calculation only occurs when the
  * addition of a solution exceeds the capacity.
  *
  * @see org.moeaframework.core.fitness.FitnessBasedArchive
  */
public class DoubleComparatorArchive extends AbstractNonDominatedPopulation {


    /**
     * The maximum capacity of this archive.
     */
    private final int capacity;

    /**
     * The fitness evaluator for computing the fitness of solutions.
     */
    private final FitnessEvaluator fitnessEvaluator;

    /**
     * The fitness comparator for comparing fitness values.
     */
    private final Comparator<Solution> fitnessComparator;

    /**
     * Constructs an empty fitness-based archive.
     *
     * @param evaluator the fitness evaluator for computing the fitness of
     *        solutions
     * @param capacity the maximum capacity of this archive
     */
    public DoubleComparatorArchive(FitnessEvaluator evaluator, int capacity) {
        this(evaluator, capacity, new ParetoDominanceComparator(), new WSumComparator(evaluator.areLargerValuesPreferred()));
    }

    /**
     * Constructs an empty fitness-based archive.
     *
     * @param evaluator the fitness evaluator for computing the fitness of
     *        solutions
     * @param capacity the maximum capacity of this archive
     * @param comparator the dominance comparator
     */
    public DoubleComparatorArchive(FitnessEvaluator evaluator, int capacity, DominanceComparator comparator, Comparator<Solution> fitnessComparator) {
        super(comparator);

        this.fitnessEvaluator = evaluator;
        this.capacity = capacity;

        this.fitnessComparator = fitnessComparator;
    }

    @Override
    public boolean add(Solution solution) {
        boolean solutionAdded = super.add(solution);

        if (solutionAdded) {
            if (size() > capacity) {
                update();
                truncate(capacity, fitnessComparator);
            }
        }

        return solutionAdded;
    }

    /**
     * Updates the fitness of all solutions in this population.
     */
    public void update() {
        fitnessEvaluator.evaluate(this);
    }

    @Override
    public Solution getBest() {
        update();
        if(this.size() == 0) {
            throw new IllegalStateException("Invalid call");
        }
        Solution pBest = this.get(0);
        if(this.size() == 1) {
            return pBest;
        }
        for(int i = 0; i < this.size() - 1; i++) {
            int flag = fitnessComparator.compare(this.get(i + 1),
                    pBest);

            if (flag <= 0) {
                pBest = this.get(i);
            }
        }
        return pBest;
    }
}
