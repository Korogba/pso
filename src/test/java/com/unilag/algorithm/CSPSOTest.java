package com.unilag.algorithm;

import com.unilag.RetryOnTravis;
import com.unilag.TravisRunner;
import com.unilag.comparator.WSumComparator;
import com.unilag.fitness.WSumFitnessEvaluator;
import com.unilag.interfaces.AbstractPSO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the {@link CSPSO} class.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class CSPSOTest extends AlgorithmTest {
    @Test
    public void testDTLZ1() throws IOException {
        test("DTLZ1_2", "CSPSO", "SMPSO-JMetal");
    }

    @Test
    public void testDTLZ2() throws IOException {
        test("DTLZ2_2", "CSPSO", "SMPSO-JMetal");
    }

    @Test
    public void testDTLZ7() throws IOException {
        test("DTLZ7_2", "CSPSO", "SMPSO");
    }

    @Test
    public void testUF1() throws IOException {
        test("UF1", "CSPSO", "SMPSO");
    }

    /**
     * Tests that the {@link AbstractPSO#selectLeader(int)} method correctly detects
     * and returns the leader with the highest weighted sum value given the list of
     * randomly selected leaders
     * Weighted value of A: 3.5925
     * Weighted value of B: 1.3684
     * Weighted value of C: 5.5294
     * Weighted value of D: 7.3
     */
    @Test
    public void leadershipSelectionTest(){
        Solution solutionA = new Solution(new double[] {10, 17});
        Solution solutionB = new Solution(new double[] {1, 18});
        Solution solutionC = new Solution(new double[] {11, 6});
        Solution solutionD = new Solution(new double[] {18, 2});

        Solution particle = new Solution(new double[] {8, 1});

        Solution[] solutions = {solutionA, solutionB, solutionC, solutionD};
        Population population = new Population(solutions);

        WSumFitnessEvaluator wSumFitnessEvaluator = new WSumFitnessEvaluator(particle);
        wSumFitnessEvaluator.evaluate(population);

        WSumComparator leaderComparator = new WSumComparator(true);

        List<Solution> leadersList = new ArrayList<>(Arrays.asList(solutionA, solutionB, solutionC, solutionD));
        leadersList.sort(leaderComparator);

        Assert.assertEquals(leadersList.get(0), solutionD);

    }
}
