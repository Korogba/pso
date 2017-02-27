#!/usr/bin/env bash
NSAMPLES=35 #MOEAD number of samples is 25!
NSEEDS=25
METHOD=Saltelli
PROBLEM=DTLZ2_8
ALGORITHMS=( MOEAD )
SEEDS=$(seq 1 ${NSEEDS})
JAVA_ARGS="-classpath .:lib -Djava.ext.dirs=lib -Xmx1024m"

#Uncomment the following commands if the Pareto front of the algorithm is not known and a Reference
#set is generated from all the approximation sets

# Generate the parameter samples: Samples already generated - uncomment if untrue
echo -n "Generating parameter samples..."
for ALGORITHM in ${ALGORITHMS[@]}
do
  java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.SampleGenerator -m ${METHOD} -n ${NSAMPLES} -p analysis/${ALGORITHM}_Params -o analysis/${ALGORITHM}_${METHOD}
done
echo "done."

# Generate the combined approximation sets for each algorithm
#for ALGORITHM in ${ALGORITHMS[@]}
#do
#    echo -n "Generating combined approximation set for ${ALGORITHM}..."
#    java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.ResultFileMerger -b ${PROBLEM} -o  analysis/${ALGORITHM}_${PROBLEM}.combined  analysis/${ALGORITHM}_${PROBLEM}_*.set
#    echo "done."
#done

# Generate the reference set from all combined approximation sets
#echo -n "Generating reference set..."
#    java ${JAVA_ARGS} org.moeaframework.util.ReferenceSetMerger -o  analysis/${PROBLEM}_combined.reference  analysis/*_${PROBLEM}.combined > /dev/null
#echo "done."
#
## Calculate set contribution
#echo ""
#echo "Set contribution:"
#java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.SetContribution -r analysis/${PROBLEM}_combined.reference analysis/*_${PROBLEM}.combined
