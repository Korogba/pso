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
import org.moeaframework.core.comparator.ParetoDominanceComparator;

import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Tests the {@link CDASObjectiveComparator} class.
 */
public class CDASObjectiveComparatorTest {

    /**
     * The comparators used for testing.
     */
    private CDASComparator areaIncreaseComparator;
    private CDASComparator paretoComparator;
    private CDASComparator areaDecreaseComparator;
    private ParetoDominanceComparator dominanceComparator;

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
        areaIncreaseComparator = new CDASComparator(0.25);
        areaDecreaseComparator = new CDASComparator(0.75);
        paretoComparator = new CDASComparator(0.5);
        dominanceComparator = new ParetoDominanceComparator();
    }

    /**
     * Removes references to shared objects so they can be garbage collected.
     */
    @After
    public void tearDown() {
        areaIncreaseComparator = null;
        dominanceComparator = null;
    }

    /**
     * Tests if cdas comparators correctly maintains dominance.
     */
    @Test
    public void testDominance() {
        //Assert that pareto dominance detects non-dominance
        Assert.assertTrue(dominanceComparator.compare(solutionE, solutionA) < 0);

        //Assert that CDAS detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionE, solutionA) < 0);

        //Assert that CDAS detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionE, solutionC) < 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionE, solutionA) <= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionE, solutionB) <= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionE, solutionC) <= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionE, solutionD) <= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaIncreaseComparator maintains the dominance relation
        Assert.assertTrue(areaIncreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionE, solutionA) <= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionE, solutionB) <= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionE, solutionC) <= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionE, solutionD) <= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionA, solutionE) >= 0);

        //Assert that CDAS areaDecreaseComparator maintains the dominance relation
        Assert.assertTrue(areaDecreaseComparator.compare(solutionA, solutionE) >= 0);
    }

    /*
    * Tests if cdas comparators correctly expands or shrinks the area of dominance
    * given the user defined parameter
    * A: r=19.723083, w={59.53445521158, 30.465545298165}
    *   User supplied Parameter: 0.25=> A = {27.000000088967326, 27.00000015124443}
    *                            0.75=> A = {-7.000000088967313, 6.999999848755643}
    *
    * B: r=18.027756 w={86.820169813514, 3.179808534236}
    *   User supplied Parameter: 0.25=> B = {18.999999622098127, 18.999993197745013}
    *                            0.75=> B = {-16.999999622098326, 17.000006802254976}
    * C: r=12.529964 w={28.610458943815, 61.38954011918}
    *   User supplied Parameter: 0.25=> C = {16.999999820107995, 16.999999901877107}
    *                            0.75=> C = {5.000000179892014, -4.9999999018770325}
    * D: r= 18.11077 w={6.340183879622, 83.659808156975}
    *   User supplied Parameter: 0.25=> D = {19.999997498223347, 19.99999972202513}
    *                            0.75=> D = {16.000002501776677, -15.99999972202483}
    * E: r= 2.236068 w = {63.434949111189, 26.565052330144}
    *   User supplied Parameter: 0.25=> D = {3.000000025155992, 3.000000050311998}
    *                            0.75=> D = {-1.0000000251560246, 0.9999999496880045}
    */
    @Test
    public void testCDAS(){

        //Assert that A and B are non-dominated
        Assert.assertTrue(dominanceComparator.compare(solutionA, solutionB) == 0);

        //Assert that CDAS areaIncreaseComparator increases the area of B to dominate A
        Assert.assertTrue(areaIncreaseComparator.compare(solutionA, solutionB) > 0);

        //Assert that CDAS areaIncreaseComparator increases the area of B to dominate A
        Assert.assertTrue(areaIncreaseComparator.compare(solutionB, solutionA) < 0);

        //Assert that C and B are non-dominated
        Assert.assertTrue(dominanceComparator.compare(solutionC, solutionB) == 0);

        //Assert that CDAS areaIncreaseComparator increases the area of C to dominate B
        Assert.assertTrue(areaIncreaseComparator.compare(solutionC, solutionB) < 0);

        //Assert that C and D are non-dominated
        Assert.assertTrue(dominanceComparator.compare(solutionC, solutionD) == 0);

        //Assert that CDAS areaIncreaseComparator increases the area of C to dominate D
        Assert.assertTrue(areaIncreaseComparator.compare(solutionC, solutionD) < 0);

        //Assert that E dominates A
        Assert.assertTrue(dominanceComparator.compare(solutionE, solutionA) < 0);

        //Assert that CDAS areaDecreaseComparator decreases the area of E to be non-dominant wrt A
        Assert.assertTrue(areaDecreaseComparator.compare(solutionE, solutionA) == 0);

    }

    /**
     * Tests if cdas comparators correctly detects non-dominance.
     */
    @Test
    public void testNondominance() {
        //Assert that pareto dominance areaIncreaseComparator detects non-dominance
        Assert.assertTrue(dominanceComparator.compare(solutionA, solutionB) == 0);

        //Assert that CDAS comparator detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionA, solutionB) == 0);

        //Assert that CDAS comparator detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionB, solutionA) == 0);

        //Assert that CDAS comparator detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionC, solutionD) == 0);

        //Assert that CDAS comparator detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionA, solutionC) == 0);

        //Assert that CDAS comparator detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionB, solutionC) == 0);

        //Assert that CDAS comparator detects non-dominance in user-supplied parameter of 0.5
        Assert.assertTrue(paretoComparator.compare(solutionB, solutionD) == 0);
    }

    /**
     * Conduct integration tests with {@link com.unilag.fitness.ShiftBasedDensityEvaluator} and
     * {@link com.unilag.fitness.DoubleComparatorArchive}
     */
    @Test
    public void testIntegration(){
        ShiftBasedDensityEvaluator evaluator = new ShiftBasedDensityEvaluator();
        Solution[] solutions = {solutionA, solutionB, solutionC, solutionD};
        Population population = new Population(solutions);

        DoubleComparatorArchive doubleComparatorArchive = new DoubleComparatorArchive(evaluator,
                2, areaDecreaseComparator, new FitnessComparator(evaluator.areLargerValuesPreferred()));

        for(Solution solution : population) {
            doubleComparatorArchive.add(solution);
        }

        //Assert the archive prunes the archive size when full
        Assert.assertEquals(doubleComparatorArchive.size(), 2);

        //Reducing the area of dominance results in selection-pressure loss and SDE plays major role
        Assert.assertThat(doubleComparatorArchive, containsInAnyOrder(solutionB, solutionD));

        DoubleComparatorArchive doubleComparatorArchiveIncrease = new DoubleComparatorArchive(evaluator,
                2, areaIncreaseComparator, new FitnessComparator(evaluator.areLargerValuesPreferred()));

        for(Solution solution : population) {
            doubleComparatorArchiveIncrease.add(solution);
        }

        //Assert the archive prunes the archive size when full
        Assert.assertEquals(doubleComparatorArchiveIncrease.size(), 1);

        //Increasing the area of dominance results in selection-pressure gain and CDAS plays major role
        Assert.assertThat(doubleComparatorArchiveIncrease, containsInAnyOrder(solutionC));
    }
}
