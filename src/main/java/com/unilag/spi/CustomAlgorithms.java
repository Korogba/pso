package com.unilag.spi;

import com.unilag.algorithm.CSPSO;
import org.moeaframework.algorithm.pso.SMPSO;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;

import java.util.Properties;

/**
 * A provider of custom algorithms. The following table contains all
 * available algorithms and the customizable properties.
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="10%" align="left">Name</th>
 *     <th width="10%" align="left">Type</th>
 *     <th width="80%" align="left">Properties</th>
 *   </tr>
 *   <tr>
 *     <td>CSPSO</td>
 *     <td>Real</td>
 *     <td>{@code populationSize, archiveSize, pm.rate,
 *         pm.distributionIndex, userDefinedParameter}</td>
 *   </tr>
 * </table>
 *
 * @see org.moeaframework.algorithm.StandardAlgorithms
 */
public class CustomAlgorithms extends AlgorithmProvider {

    /**
     * Constructs a CustomAlgorithms following the SPI software design principle
     */
    public CustomAlgorithms() {
        super();
    }

    @Override
    public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
        TypedProperties typedProperties = new TypedProperties(properties);

        try {
            if(name.equalsIgnoreCase("CSPSO")) {
                return newCSPSO(typedProperties, problem);

            } else {
                return null;
            }
        } catch (FrameworkException e) {
            throw new ProviderNotFoundException(name, e);
        }
    }

    /**
     * Returns a new {@link SMPSO} instance.
     *
     * @param properties the properties for customizing the new {@code SMPSO}
     *        instance
     * @param problem the problem
     * @return a new {@code SMPSO} instance
     */
    private Algorithm newCSPSO(TypedProperties properties, Problem problem) {
        if (!checkType(RealVariable.class, problem)) {
            throw new FrameworkException("unsupported decision variable type");
        }

        int populationSize = (int)properties.getDouble("populationSize", 100);
        int archiveSize = (int)properties.getDouble("archiveSize", 100);
        double userDefinedParameter = 0.25;
        double mutationProbability = properties.getDouble("pm.rate",
                1.0 / problem.getNumberOfVariables());
        double distributionIndex = properties.getDouble("pm.distributionIndex",
                20.0);

        return new CSPSO(problem, populationSize, archiveSize,
                mutationProbability, distributionIndex, userDefinedParameter);
    }

    /**
     * Returns {@code true} if all decision variables are assignment-compatible
     * with the specified type; {@code false} otherwise.
     *
     * @param type the type of decision variable
     * @param problem the problem
     * @return {@code true} if all decision variables are assignment-compatible
     *         with the specified type; {@code false} otherwise
     */
    private boolean checkType(Class<? extends Variable> type, Problem problem) {
        Solution solution = problem.newSolution();

        for (int i=0; i<solution.getNumberOfVariables(); i++) {
            if (!type.isInstance(solution.getVariable(i))) {
                return false;
            }
        }

        return true;
    }
}
