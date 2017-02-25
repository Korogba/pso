package com.unilag.fitness;

import com.unilag.helper.Utils;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.List;

public class ShiftBasedDensityEvaluator implements FitnessEvaluator {

    /**
     * Constructs a new shift-based density fitness evaluator.
     */
    public ShiftBasedDensityEvaluator() {
        super();
    }

    /**
     * Evaluates the solutions in the specified population assigning the
     * {@code FITNESS_ATTRIBUTE} attribute.
     * This attribute will be calculated using:
     *
     *  * References:
     * <ol>
     *   <li>Li, M., Yang, S., & Liu, X. (2013).
     *       Shift-Based Density Estimation for Pareto-Based Algorithms in Many-Objective Optimization .
     *       In  Transactions on Evolutionary Computation
     *       (pp. 348 - 365) IEEE.
     *   </li>
     * </ol>
     *
     *  * Algorithm:
     *  * Input Population: P, Size of population: N
     * <ol>
     *   <li>For each individual s in the population P
     *      <ol>for each objective j:
     *          <li>shiftedList = List of size (N-1)</li>
     *          <ol>
     *              <li> for each individual q where q ε P & p != q
     *                  <ol>
     *                      <li>Calculate the shifted version of q′ for objective j: q′(j), given as below</li>
     *                      <li>q′(j) = (q(j) < p(j)) p(j) : q(j)</li>
     *                  </ol>
     *                  <li>shiftedList += q′</li>
     *              </li>
     *          </ol>
     *      </ol>
     *      <ol>for each individual in shiftedList, r′
     *          <li>dist(p, r′) = euclidean distance between p & r</li>
     *      </ol>
     *      <li>Calculate: D(p, P) = D(dist(p, q′(1)), dist(p, q′(2)), ..., dist(p, q′(N−1)))</li>
     *      <li>Set SHIFT_BASED_DENSITY_ESTIMATOR attribute for p</li>
     *   </li>
     * </ol>
     *
     * @param population the population to be evaluated
     */
    @Override
    public void evaluate(Population population) {

        for(Solution individual : population) {
            List<Solution> solutionList = getShiftedPopulationForIndividual(individual, copy(population));
            double shiftValue = 0;
            for(Solution shiftIndividual : solutionList) {
                shiftValue += Utils.euclideanDistance(individual, shiftIndividual);
            }
            individual.setAttribute(FITNESS_ATTRIBUTE, shiftValue);
        }
    }

    List<Solution> getShiftedPopulationForIndividual(Solution current, Population population) {
        Population truncatedPopulation = copy(population);
        truncatedPopulation.remove(current);
        truncatedPopulation = deepCopy(truncatedPopulation);
        List<Solution> shiftedSolutions = new ArrayList<>();
        for(Solution individual: truncatedPopulation){
            if(current.equals(individual)) {
                continue;
            }
            for(int i = 0; i < current.getNumberOfObjectives(); i++) {
                ObjectiveComparator objectiveComparator = new ObjectiveComparator(i);
                double shiftedValue = objectiveComparator.compare(individual, current) < 0 ?
                        current.getObjective(i) : individual.getObjective(i);
                individual.setObjective(i, shiftedValue);
            }
            shiftedSolutions.add(individual);
        }
        return shiftedSolutions;
    }

    /**
     * Returns a copy of the population. The population solutions contains references to the supplied
     * population and alterations will change the supplied population
     * @param population the original population
     * @return a copy of the population
     */
    private Population copy(Population population) {
        Population result = new Population();

        for (Solution solution : population) {
            result.add(solution);
        }

        return result;
    }

    /**
     * Returns a copy of the population.  The density estimator changes the
     * objectives of the individual solutions, so a copy of the population
     * is used to ensure the original population remains intact:
     * Changes to the returned population solutions will not
     * affect the supplied population
     * @param population the original population
     * @return a copy of the population
     */
    private Population deepCopy(Population population) {
        Population result = new Population();

        for (Solution solution : population) {
            result.add(solution.copy());
        }

        return result;
    }

    /**
     * Returns {@code true} if larger fitness values are preferred; otherwise
     * smaller fitness values are preferred.
     *
     * @return {@code true} if larger fitness values are preferred; otherwise
     *         smaller fitness values are preferred
     */
    @Override
    public boolean areLargerValuesPreferred() {
        return true;
    }
}
