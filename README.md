# Covid_Simulation_Model

![Java CI with Gradle](https://github.com/ScottishCovidResponse/Covid_Simulation_Model/workflows/Java%20CI%20with%20Gradle/badge.svg)

## Background

This model is developed for the COVID-19 RMAP response (https://royalsociety.org/topics-policy/Health%20and%20wellbeing/ramp/) to simulate the spread of Covid-19 through a community. In the first instance we envisage a community o a similar scalt to the City of Glasgow. The intention is that it stochastically simulates the movements of people in near real time and their interactions and the spread through these mixing populations. We can subsequently simulate the implementation fo controls in these populations. 

## Building and Running

### Java SDK

The Covid_Simulation_Model targets Java 11, please ensure you have a Java SDK installed.

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

The built java project takes two positional commandline arguments. The first is
a json file defining the disease and population parameters, and the second is a
json file defining the parameters of a particular run, for example, the number
of iterations. Example json files and further documentation are provided in the
`parameters` folder.

To run the project:
```shell script
gradle run  --args "parameters/example_population_params.json parameters/example_model_params.json"
```

The run command optionally takes an integer to be used as the seed for the
random number generator (such that two identically seeded runs will return the
same result).

The result csv will be placed into the file specified by the `outputFile` parameter
in the model parameters.

## Version History


