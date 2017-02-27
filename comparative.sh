#!/usr/bin/env bash
NSAMPLES=35
NSEEDS=25
METHOD=Saltelli
PROBLEM=DTLZ2_8
ALGORITHMS=( CSPSOII CDASGDE )
SEEDS=$(seq 1 ${NSEEDS})
JAVA_ARGS="-classpath .:lib -Djava.ext.dirs=lib -Xmx1024m"

# Clear old data
echo -n "Clearing old data (if any)..."
#rm analysis/*_${PROBLEM}_*.set
#rm analysis/*_${PROBLEM}_*.metrics
echo "done."
# set -e

# Generate the parameter samples: Samples already generated - uncomment if untrue
#echo -n "Generating parameter samples..."
#for ALGORITHM in ${ALGORITHMS[@]}
#do
#  java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.SampleGenerator -m ${METHOD} -n ${NSAMPLES} -p analysis/${ALGORITHM}_Params -o analysis/${ALGORITHM}_${METHOD}
#done
#echo "done."

# Evaluate all algorithms for all seeds
for ALGORITHM in ${ALGORITHMS[@]}
do
    echo "Evaluating ${ALGORITHM}:"
    for SEED in ${SEEDS}
    do
        echo -n " Processing seed ${SEED}..."
        java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.Evaluator -p  analysis/${ALGORITHM}_Params -i  analysis/${ALGORITHM}_${METHOD} -b ${PROBLEM} -a ${ALGORITHM} -s ${SEED} -o  analysis/${ALGORITHM}_${PROBLEM}_${SEED}.set
        echo "done."
    done
done

#Uncomment the following commands if the Pareto front of the algorithm is not known and a Reference
#set is generated from all the approximation sets

# Generate the combined approximation sets for each algorithm
for ALGORITHM in ${ALGORITHMS[@]}
do
    echo -n "Generating combined approximation set for ${ALGORITHM}..."
    java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.ResultFileMerger -b ${PROBLEM} -o  analysis/${ALGORITHM}_${PROBLEM}.combined  analysis/${ALGORITHM}_${PROBLEM}_*.set
    echo "done."
done

# Generate the reference set from all combined approximation sets
#echo -n "Generating reference set..."
#    java ${JAVA_ARGS} org.moeaframework.util.ReferenceSetMerger -o  analysis/${PROBLEM}.reference  analysis/*_${PROBLEM}.combined > /dev/null
#echo "done."

# Evaluate the performance metrics
for ALGORITHM in ${ALGORITHMS[@]}
do
    echo "Calculating performance metrics for ${ALGORITHM}:"
    for SEED in ${SEEDS}
    do
        echo -n " Processing seed ${SEED}..."
        java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.ResultFileEvaluator -b ${PROBLEM} -i  analysis/${ALGORITHM}_${PROBLEM}_${SEED}.set -r analysis/${PROBLEM}.reference -o analysis/${ALGORITHM}_${PROBLEM}_${SEED}.metrics
        echo "done."
    done
done

# Average the performance metrics across all seeds
for ALGORITHM in ${ALGORITHMS[@]}
do
    echo -n "Averaging performance metrics for ${ALGORITHM}..."
    java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.SimpleStatistics -m average -o analysis/${ALGORITHM}_${PROBLEM}.average  analysis/${ALGORITHM}_${PROBLEM}_*.metrics
    echo "done."
done

# Perform the analysis
echo ""
echo "Analysis:"
for ALGORITHM in ${ALGORITHMS[@]}
do
    java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.Analysis -p analysis/${ALGORITHM}_Params -i analysis/${ALGORITHM}_${METHOD} -m 1  analysis/${ALGORITHM}_${PROBLEM}.average
done

# Calculate set contribution
echo ""
echo "Set contribution:"
java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.SetContribution -r analysis/${PROBLEM}.reference analysis/*_${PROBLEM}.combined

# Calculate Sobol sensitivities
if [ ${METHOD} == "Saltelli" ]
then
    for ALGORITHM in ${ALGORITHMS[@]}
    do
        echo ""
        echo "Sobol sensitivities for ${ALGORITHM}"
        java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.SobolAnalysis -p  analysis/${ALGORITHM}_Params -i  analysis/${ALGORITHM}_${PROBLEM}.average -m 1
    done
fi