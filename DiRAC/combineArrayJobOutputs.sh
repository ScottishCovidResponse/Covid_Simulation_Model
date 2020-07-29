#!/bin/bash

if [ $# -lt 3 ]
then
	echo "Usage: $0 jobID lastArrayIndex outputFileName"
	exit 1
fi

jobID=$1
lastArrayIndex=$2
outputFile=$3

# copy first file including header
cp job_${jobID}_0/$outputFile $outputFile

# append other files with header stripped
for ((i = 1 ; i <= $lastArrayIndex ; i++))
do
	nextFile="job_${jobID}_$i/$outputFile"
	tail -n +2 $nextFile >> $outputFile
done
