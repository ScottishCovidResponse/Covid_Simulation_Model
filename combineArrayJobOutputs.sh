#!/bin/bash

if [ $# -le 2 ]
then
	echo "Usage: $0 jobID lastArrayIndex"
	exit 1
fi

jobID=$1
lastArrayIndex=$2

# copy first file including header
cp job_${jobID}_0/example.csv example.csv

# append other files with header stripped
for ((i = 1 ; i <= $lastArrayIndex ; i++))
do
	nextFile="job_${jobID}_${i}/example.csv"
	tail -n +2 $nextFile >> example.csv
done

