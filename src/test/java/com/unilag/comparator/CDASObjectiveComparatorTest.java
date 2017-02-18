package com.unilag.comparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Tests the {@link SCDASObjectiveComparator} class.
 */
public class CDASObjectiveComparatorTest {

    private static final Logger log = LoggerFactory.getLogger(CDASObjectiveComparatorTest.class);

    /**
     * The &epsilon;-box objective comparator used for testing.
     */
    private CDASComparator comparator;
    private ParetoDominanceComparator dominanceComparator;

    private Solution solutionOne;
    private Solution solutionTwo;
    private Solution solutionThree;


    /**
     * Setup the comparator for use by all test methods.
     */
    @Before
    public void setUp() {
        solutionOne = new Solution(new double[] { 6, 8 });
        solutionTwo = new Solution(new double[] { 4, 9 });
        solutionThree = new Solution(new double[] { 3, 1 });
        Solution[] particles = {solutionOne, solutionTwo, solutionThree};
        comparator = new CDASComparator(particles, 0.25);
        dominanceComparator = new ParetoDominanceComparator();
//        log.info("Modified Origin: " + Arrays.toString(comparator.getCdasObjectiveComparator().getModifiedOrigin()));
//        log.info("Landmark Vectors: " + Arrays.deepToString(comparator.getCdasObjectiveComparator().getLandmarkVectors()));
//        log.info("Phi Angles: " + Arrays.deepToString(comparator.getCdasObjectiveComparator().getPhiAngle()));
        double[] oneAttribute = (double[]) solutionOne.getAttribute(CDASObjectiveComparator.CDAS_FITNESS_ATTRIBUTE);
        double[] twoAttribute = (double[]) solutionTwo.getAttribute(CDASObjectiveComparator.CDAS_FITNESS_ATTRIBUTE);
        double[] threeAttribute = (double[]) solutionThree.getAttribute(CDASObjectiveComparator.CDAS_FITNESS_ATTRIBUTE);
        log.info("Solution One: " + Arrays.toString(oneAttribute));
        log.info("Solution Two: " + Arrays.toString(twoAttribute));
        log.info("Solution Three: " + Arrays.toString(threeAttribute));

    }

    /**
     * Removes references to shared objects so they can be garbage collected.
     */
    @After
    public void tearDown() {
        comparator = null;
        dominanceComparator = null;
    }

    /**
     * Tests if the comparator correctly detects dominance.
     */
    @Test
    public void testDominance() {

//        Assert.assertTrue(comparator.compare(solutionOne, solutionTwo) < 0);
//        Assert.assertTrue(dominanceComparator.compare(solutionOne, solutionTwo) < 0);
//        Assert.assertTrue(comparator.compare(solutionOne, solutionThree) < 0);
//        Assert.assertTrue(dominanceComparator.compare(solutionOne, solutionThree) < 0);
//        Assert.assertFalse(comparator.compare(solutionTwo, solutionThree) < 0);
//        Assert.assertFalse(dominanceComparator.compare(solutionTwo, solutionThree) < 0);
//        Assert.assertFalse(comparator.compare(solutionThree, solutionTwo) > 0);
//        Assert.assertFalse(dominanceComparator.compare(solutionThree, solutionTwo) > 0);

//        Assert.assertTrue(comparator.compare(solution2, solution1) > 0);
//        Assert.assertFalse(comparator.isSameBox());
//
//        Assert.assertFalse(comparator.isSameBox());
//        Assert.assertTrue(comparator.compare(solution3, solution1) > 0);
//        Assert.assertFalse(comparator.isSameBox());
//        Assert.assertFalse(comparator.isSameBox());
//
//        Assert.assertFalse(comparator.isSameBox());
    }

    /**
     * Tests if the comparator correctly detects non-dominance.
     */
//    @Test
//    public void testNondominance() {
//        Solution solution1 = new Solution(new double[] { 0.75, 0.25 });
//        Solution solution2 = new Solution(new double[] { 0.25, 0.75 });

//        Assert.assertTrue(comparator.compare(solution1, solution2) == 0);
//        Assert.assertFalse(comparator.isSameBox());
//        Assert.assertTrue(comparator.compare(solution2, solution1) == 0);
//        Assert.assertFalse(comparator.isSameBox());
//    }

    /**
     * Test if an {@code EpsilonBoxObjectiveComparator} correctly detects
     * dominance within the same &epsilon;-box (i.e., selects the solution
     * nearer to the optimal corner).
     */
//    @Test
//    public void testDominanceInBox() {
//        Solution solution1 = new Solution(new double[] { 0.15, 0.35 });
//        Solution solution2 = new Solution(new double[] { 0.2, 0.2 });

//        Assert.assertTrue(comparator.compare(solution1, solution2) > 0);
//        Assert.assertTrue(comparator.isSameBox());
//        Assert.assertTrue(comparator.compare(solution2, solution1) < 0);
//        Assert.assertTrue(comparator.isSameBox());
//    }

    /**
     * Tests if an {@code EpsilonBoxObjectiveComparator} correctly extends the
     * epsilon array.
     */
//    @Test
//    public void testEpsilonExtension() {
//        EpsilonBoxDominanceComparator comparator =
//                new EpsilonBoxDominanceComparator(new double[] { 0.1, 0.2 });

//        Assert.assertEquals(0.1, comparator.getEpsilon(0), Settings.EPS);
//        Assert.assertEquals(0.2, comparator.getEpsilon(1), Settings.EPS);
//        Assert.assertEquals(0.2, comparator.getEpsilon(2), Settings.EPS);
//        Assert.assertEquals(0.2, comparator.getEpsilon(Integer.MAX_VALUE),
//                Settings.EPS);
//        Assert.assertEquals(2, comparator.getNumberOfDefinedEpsilons());
//    }
}
