package com.unilag.helper;

import org.moeaframework.core.Solution;

/**
 * Created by @kaba_y on 15/01/2017
 * Utility class to hold miscellaneous functions used by the PSO
 */
public class Utils {

    public static double euclideanDistance(Solution individual, Solution shiftIndividual) throws ArrayIndexOutOfBoundsException {
        double sum = 0;
        for(int i = 0; i < individual.getNumberOfObjectives(); i++) {
            sum += Math.pow((individual.getObjective(i) - shiftIndividual.getObjective(i)), 2);
        }

        return Math.sqrt(sum);
    }
}
