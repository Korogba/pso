package com.unilag.fitness;

import com.unilag.comparator.CDASObjectiveComparatorTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.fitness.FitnessBasedArchive;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;

/**
 * Tests the {@link ShiftBasedDensityEvaluator} class.
 */
public class ShiftBasedDensityEvaluatorTest {

    private ShiftBasedDensityEvaluator shiftBasedDensityEvaluator;
    private Solution solutionA;
    private Solution solutionB;
    private Solution solutionC;
    private Solution solutionD;
    private Population population;



    /**
     * Setup the Fitness evaluator for use by all test methods.
     */
    @Before
    public void setUp() {
        shiftBasedDensityEvaluator = new ShiftBasedDensityEvaluator();
        solutionA = new Solution(new double[] {10, 17});
        solutionB = new Solution(new double[] {1, 18});
        solutionC = new Solution(new double[] {11, 6});
        solutionD = new Solution(new double[] {18, 2});
        Solution[] solutions = {solutionA, solutionB, solutionC, solutionD};
        population = new Population(solutions);
    }

    /**
     * Removes references to shared objects so they can be garbage collected.
     */
    @After
    public void tearDown() {
        shiftBasedDensityEvaluator = null;
        solutionA = null;
        solutionB = null;
        solutionC = null;
        solutionD = null;
    }



    /**
     * Tests the {@link ShiftBasedDensityEvaluator}'s getShiftedPopulationForIndividual() method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testShiftPopulation() {
        List<Solution> aShifts = shiftBasedDensityEvaluator.getShiftedPopulationForIndividual(solutionA, population);

        //Assert that the appropriate size is returned
        Assert.assertEquals(aShifts.size(), 3);

        double[] ashiftB = {10, 18};
        double[] ashiftC = {11, 17};
        double[] ashiftD = {18, 17};

        //Assert that the appropriate values are returned
        Assert.assertThat(aShifts, containsInAnyOrder(hasProperty("objectives", is(ashiftB)),
                hasProperty("objectives", is(ashiftC)),
                hasProperty("objectives", is(ashiftD))));

        //Assert that the original population is not modified
        Assert.assertThat(population, containsInAnyOrder(solutionA, solutionB, solutionC, solutionD));

        List<Solution> bShifts = shiftBasedDensityEvaluator.getShiftedPopulationForIndividual(solutionB, population);

        //Assert that the appropriate size is returned
        Assert.assertEquals(aShifts.size(), 3);

        double[] bshiftA = {10, 18};
        double[] bshiftC = {11, 18};
        double[] bshiftD = {18, 18};

        //Assert that the appropriate values are returned
        Assert.assertThat(bShifts, containsInAnyOrder(hasProperty("objectives", is(bshiftA)),
                hasProperty("objectives", is(bshiftC)),
                hasProperty("objectives", is(bshiftD))));

        //Assert that the original population is not modified
        Assert.assertThat(population, containsInAnyOrder(solutionA, solutionB, solutionC, solutionD));

        List<Solution> cShifts = shiftBasedDensityEvaluator.getShiftedPopulationForIndividual(solutionC, population);

        //Assert that the appropriate size is returned
        Assert.assertEquals(aShifts.size(), 3);

        double[] cshiftA = {11, 17};
        double[] cshiftB = {11, 18};
        double[] cshiftD = {18, 6};

        //Assert that the appropriate values are returned
        Assert.assertThat(cShifts, containsInAnyOrder(hasProperty("objectives", is(cshiftA)),
                hasProperty("objectives", is(cshiftB)),
                hasProperty("objectives", is(cshiftD))));

        //Assert that the original population is not modified
        Assert.assertThat(population, containsInAnyOrder(solutionA, solutionB, solutionC, solutionD));

        List<Solution> dShifts = shiftBasedDensityEvaluator.getShiftedPopulationForIndividual(solutionD, population);

        //Assert that the appropriate size is returned
        Assert.assertEquals(aShifts.size(), 3);

        double[] dshiftA = {18, 17};
        double[] dshiftB = {18, 18};
        double[] dshiftC = {18, 6};

        //Assert that the appropriate values are returned
        Assert.assertThat(dShifts, containsInAnyOrder(hasProperty("objectives", is(dshiftA)),
                hasProperty("objectives", is(dshiftB)),
                hasProperty("objectives", is(dshiftC))));

        //Assert that the original population is not modified
        Assert.assertThat(population, containsInAnyOrder(solutionA, solutionB, solutionC, solutionD));

    }

    /**
     * Tests that {@link ShiftBasedDensityEvaluator} properly prunes a {@link FitnessBasedArchive} when
     * a solution is added to exceed the {@link FitnessBasedArchive}'s size
     * Note the use of the default Pareto Comparator here: All solutions are non-dominated wrt to each other
     * For integration tests with CDAS, {@link CDASObjectiveComparatorTest#testIntegration()} ()}
     * Actual shift-based values of A = 1 + 1 + 8 = 10
     * Actual shift-based values of B = 9 + 10 + 17 = 36
     * Actual shift-based values of C = 11 + 12 + 7 = 30
     * Actual shift-based values of D = 15 + 16 + 4 = 34
     */
    @Test
    public void testShiftBasedDensityEstimator() {
        FitnessBasedArchive fitnessBasedArchive = new FitnessBasedArchive(shiftBasedDensityEvaluator, 2);
        fitnessBasedArchive.add(solutionA);
        fitnessBasedArchive.add(solutionB);

        //Assert that both non-dominated solutions are added
        Assert.assertEquals(fitnessBasedArchive.size(), 2);

        fitnessBasedArchive.add(solutionC);

        //Assert that size of archive was truncated to capacity: 2
        Assert.assertEquals(fitnessBasedArchive.size(), 2);

        //Assert that A is truncated as it has the least shift-based value
        Assert.assertThat(fitnessBasedArchive, containsInAnyOrder(solutionB, solutionC));

        fitnessBasedArchive.add(solutionD);

        //Assert that size of archive was truncated to capacity: 2
        Assert.assertEquals(fitnessBasedArchive.size(), 2);

        //Assert that C is truncated as it has the least shift-based value
        Assert.assertThat(fitnessBasedArchive, containsInAnyOrder(solutionB, solutionD));
    }
}
