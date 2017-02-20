package com.unilag.fitness;

import com.unilag.comparator.WSumComparator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

import static com.unilag.interfaces.SumFitnessEvaluator.WSUM_FITNESS_ATTRIBUTE;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Tests the {@link WSumFitnessEvaluator} class.
 */
public class WSumFitnessEvaluatorTest {

    private WSumFitnessEvaluator wSumFitnessEvaluator;

    private Solution solutionA;
    private Solution solutionB;
    private Solution solutionC;
    private Solution solutionD;
    private Solution particle;
    private Population population;

    /**
     * Setup the Fitness evaluator for use by all test methods.
     */
    @Before
    public void setUp() {
        solutionA = new Solution(new double[] {10, 17});
        solutionB = new Solution(new double[] {1, 18});
        solutionC = new Solution(new double[] {11, 6});
        solutionD = new Solution(new double[] {18, 2});

        particle = new Solution(new double[] {8, 1});

        Solution[] solutions = {solutionA, solutionB, solutionC, solutionD};
        population = new Population(solutions);

        wSumFitnessEvaluator = new WSumFitnessEvaluator(particle);
    }

    /**
     * Removes references to shared objects so they can be garbage collected.
     */
    @After
    public void tearDown() {
        wSumFitnessEvaluator = null;
        population = null;
        solutionA = null;
        solutionB = null;
        solutionC = null;
        solutionD = null;
        particle = null;
    }

    /**
     * Tests the {@link WSumFitnessEvaluator}'s evaluate method
     * Weighted value of A: 3.5925
     * Weighted value of B: 1.3684
     * Weighted value of C: 5.5294
     * Weighted value of D: 7.3
     */
    @Test
    public void testWSumEvaluate() {
        wSumFitnessEvaluator.evaluate(population);
        double DELTA = 0.005;

        //Assert the evaluate method assigns the appropriate values
        Assert.assertEquals((double) solutionA.getAttribute(WSUM_FITNESS_ATTRIBUTE), 3.5925, DELTA);

        //Assert the evaluate method assigns the appropriate values
        Assert.assertEquals((double) solutionB.getAttribute(WSUM_FITNESS_ATTRIBUTE), 1.3684, DELTA);

        //Assert the evaluate method assigns the appropriate values
        Assert.assertEquals((double) solutionC.getAttribute(WSUM_FITNESS_ATTRIBUTE), 5.5294, DELTA);

        //Assert the evaluate method assigns the appropriate values
        Assert.assertEquals((double) solutionD.getAttribute(WSUM_FITNESS_ATTRIBUTE), 7.3, DELTA);

    }

    /**
     * Tests the {@link WSumFitnessEvaluator}'s integration with {@link DoubleComparatorArchive}
     * using {@link WSumComparator}
     */
    @Test
    public void integrationTests() {
        wSumFitnessEvaluator.evaluate(population);
        DoubleComparatorArchive fitnessBasedArchive = new DoubleComparatorArchive(wSumFitnessEvaluator, 3);

        for(Solution solution : population) {
            fitnessBasedArchive.add(solution);
        }

        //Assert the archive prunes the archive size when full
        Assert.assertEquals(fitnessBasedArchive.size(), 3);

        //Assert the archive prunes the solution with the largest weighted value
        Assert.assertThat(fitnessBasedArchive, containsInAnyOrder(solutionB, solutionA, solutionC));

        //Assert the archive selects the best leader based on WSum fitness value
        Assert.assertEquals(fitnessBasedArchive.getBest(), solutionB);

        DoubleComparatorArchive fitnessBasedArchiveSizeTwo = new DoubleComparatorArchive(wSumFitnessEvaluator, 2);

        for(Solution solution : population) {
            fitnessBasedArchiveSizeTwo.add(solution);
        }

        //Assert the archive prunes the archive size when full
        Assert.assertEquals(fitnessBasedArchiveSizeTwo.size(), 2);

        //Assert the archive prunes the solution with the largest weighted value
        Assert.assertThat(fitnessBasedArchiveSizeTwo, containsInAnyOrder(solutionB, solutionA));

        //Assert the archive selects the best leader based on WSum fitness value
        Assert.assertEquals(fitnessBasedArchiveSizeTwo.getBest(), solutionB);

    }
}
