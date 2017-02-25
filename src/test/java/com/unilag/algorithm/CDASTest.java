package com.unilag.algorithm;

import com.unilag.RetryOnTravis;
import com.unilag.TravisRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Tests the {@link com.unilag.comparator.CDASComparator} class.
 * Before testing, make certain {@link com.unilag.spi.CustomAlgorithms} returns a CDASPSO with userDefinedParameters 0.5
 * Secondly change the CDASPSO to use a FitnessBasedArchive based on {@link org.moeaframework.core.comparator.CrowdingComparator}
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class CDASTest extends AlgorithmTest {
    @Test
    public void testDTLZ1() throws IOException {
        test("DTLZ1_2", "CDASPSO", "SMPSO");
    }

    @Test
    public void testDTLZ2() throws IOException {
        test("DTLZ2_2", "CDASPSO", "SMPSO");
    }

    @Test
    public void testDTLZ7() throws IOException {
        test("DTLZ7_2", "CDASPSO", "SMPSO");
    }

    @Test
    public void testUF1() throws IOException {
        test("UF1", "CDASPSO", "SMPSO");
    }
}
