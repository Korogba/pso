package com.unilag.helper;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link Utils} class.
 */
public class UtilsTest {

    /**
     * Tests if the implementation for Euclidean distance is correct
     */
    @Test
    public void testEuclideanDistance(){
        Solution solutionOne = new Solution(new double[] { 7.5, 14, 96, 42, 6.89});
        Solution solutionTwo = new Solution(new double[] { 13, 79.8, 1.25, 36, 96});
        Solution solutionThree = new Solution(new double[] { 41, 86, 13, 85, 21});
        Solution solutionFour = new Solution(new double[] { 70, 11, 31, 46, 9});

        double firstEuclideanSum = Utils.euclideanDistance(solutionOne, solutionTwo);
        double secondEuclideanSum = Utils.euclideanDistance(solutionThree, solutionFour);

        double DELTA = 1e-5;
        Assert.assertEquals(firstEuclideanSum, 145.993303, DELTA);
        Assert.assertEquals(secondEuclideanSum, 91.951074, DELTA);

    }
}
