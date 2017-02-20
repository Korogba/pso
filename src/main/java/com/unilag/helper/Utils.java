package com.unilag.helper;

import org.moeaframework.core.Solution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by @kaba_y on 15/01/2017
 * Utility class to hold miscellaneous functions
 */
public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);


    public static double euclideanDistance(Solution individual, Solution shiftIndividual) throws ArrayIndexOutOfBoundsException {
        double sum = 0;
        for(int i = 0; i < individual.getNumberOfObjectives(); i++) {
            sum += Math.pow((individual.getObjective(i) - shiftIndividual.getObjective(i)), 2);
        }

        return Math.sqrt(sum);
    }
}
