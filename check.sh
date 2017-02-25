#!/usr/bin/env bash
JAVA_ARGS="-classpath .:lib -Djava.ext.dirs=lib -Xmx1024m"
ALGORITHMS=( CSPSO MOEAD NSGA-III )
PROBLEM=DTLZ2_10

# Generate the parameter samples
echo -n "Checking set contents..."
for ALGORITHM in ${ALGORITHMS[@]}
do
  java ${JAVA_ARGS} org.moeaframework.analysis.sensitivity.ResultFileInfo -b DTLZ2_10 analysis/${ALGORITHM}_${PROBLEM}_*.set
done
echo "done."

