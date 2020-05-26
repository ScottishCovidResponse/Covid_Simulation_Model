# Covid_Simulation_Model

![Java CI with Gradle](https://github.com/ScottishCovidResponse/Covid_Simulation_Model/workflows/Java%20CI%20with%20Gradle/badge.svg)

## Background

This model is developed as part of the [COVID-19 RAMP response](https://royalsociety.org/topics-policy/Health%20and%20wellbeing/ramp/). It simulates the spread of Covid-19 through a community. In the first instance we envisage a community of a similar scale to the City of Glasgow. The intention is that it stochastically simulates the movements of people in near real time (with a 1 hour resolution) and their interactions and the spread through these mixing populations. We can subsequently simulate the implementation of controls, such as lockdowns, in these populations.

## Building and Running

### Java SDK

The Covid_Simulation_Model targets Java 8, please ensure you have a Java SDK installed.

### Gradle

To compile, run or test this model, Gradle must be installed. This manages the libraries utilised and simplifies the build process.

#### For MacOS:
We recommend using [homebrew](www.brew.sh). 

To install homebrew:
```shell script
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
```

To install Gradle:
```shell script
brew install gradle
```

#### For Debian, Ubuntu, Mint:
```shell script
sudo apt install gradle
``` 

#### For RedHat, Centos, Fedora:
```shell script
yum install gradle
```

#### For Windows:

Follow instructions [here](https://gradle.org/install/).

## Build/Test Guide

To compile the project without running tests:
```shell script
gradle assemble
```

To compile and run the tests:
```shell script
gradle build
```

## Running a Simulation

The built java project takes three positional commandline arguments. The first is
a json file defining the disease and population parameters, the second is a
json file defining parameters of a particular run, and the third is the simulation
number for HPC array runs (which should be 0 for single runs). Example json files
and further documentation are provided in the `parameters` folder.

To run the project:
```shell script
gradle run  --args "parameters/example_population_params.json parameters/example_model_params.json 0"
```

The result csv will be placed into the file specified by the `outputFile` parameter
in the model parameters.
