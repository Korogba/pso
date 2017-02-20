package com.unilag.comparator;

import com.unilag.fitness.DoubleComparatorArchive;
import com.unilag.fitness.ShiftBasedDensityEvaluator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.fitness.CrowdingDistanceFitnessEvaluator;

import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Tests the {@link ShiftBasedDensityComparator} class.
 */
public class ShiftBasedDensityComparatorTest {

    private ShiftBasedDensityComparator shiftBasedDensityComparator;

    private Solution solutionA;
    private Solution solutionB;
    private Solution solutionC;
    private Solution solutionD;
    private Solution solutionE;

    /**
     * Setup the comparators for use by all test methods.
     */
    @Before
    public void setUp() {
        solutionA = new Solution(new double[] {10, 17});
        solutionB = new Solution(new double[] {1, 18});
        solutionC = new Solution(new double[] {11, 6});
        solutionD = new Solution(new double[] {18, 2});
        solutionE = new Solution(new double[] {1, 2});
        Solution[] solutions = {solutionA, solutionB, solutionC, solutionD, solutionE};
        Population population = new Population(solutions);
        shiftBasedDensityComparator = new ShiftBasedDensityComparator(true);
        ShiftBasedDensityEvaluator shiftBasedDensityEvaluator = new ShiftBasedDensityEvaluator();
        shiftBasedDensityEvaluator.evaluate(population);
    }

    /**
     * Removes references to shared objects so they can be garbage collected.
     */
    @After
    public void tearDown() {
        shiftBasedDensityComparator = null;
    }

    /**
     * Tests if ShiftBasedDensityComparator correctly maintains dominance.
     */
    @Test
    public void testDominance() {
        //Assert that ShiftBasedDensityComparator detects non-dominance
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionA) < 0);

        //Assert that ShiftBasedDensityComparator detects non-dominance
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionA) < 0);

        //Assert that ShiftBasedDensityComparator detects non-dominance
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionC) < 0);

        //Assert that ShiftBasedDensityComparator maintains the dominance relation
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionA) <= 0);

        //Assert that ShiftBasedDensityComparator maintains the dominance relation
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionB) <= 0);

        //Assert that ShiftBasedDensityComparator maintains the dominance relation
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionC) <= 0);

        //Assert that ShiftBasedDensityComparator maintains the dominance relation
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionE, solutionD) <= 0);

        //Assert that ShiftBasedDensityComparator maintains the dominance relation
        Assert.assertTrue(shiftBasedDensityComparator.compare(solutionA, solutionE) >= 0);
    }

    /**
     * Conduct integration tests with {@link com.unilag.fitness.ShiftBasedDensityEvaluator} and
     * {@link com.unilag.fitness.DoubleComparatorArchive}
     */
    @Test
    public void testIntegration(){
        CrowdingDistanceFitnessEvaluator evaluator = new CrowdingDistanceFitnessEvaluator();
        Solution[] solutions = {solutionA, solutionB, solutionC, solutionD};
        Population population = new Population(solutions);

        DoubleComparatorArchive doubleComparatorArchive = new DoubleComparatorArchive(evaluator,
                2, shiftBasedDensityComparator, new FitnessComparator(evaluator.areLargerValuesPreferred()));

        for(Solution solution : population) {
            doubleComparatorArchive.add(solution);
        }

        //Assert the archive prunes the archive size when full
        Assert.assertEquals(doubleComparatorArchive.size(), 1);

        //Reducing the area of dominance results in selection-pressure loss and SDE plays major role
        Assert.assertThat(doubleComparatorArchive, containsInAnyOrder(solutionB));
    }
}
