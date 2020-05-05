# Contact-Tracing-Model

## Background

## Imports from Contact Tracing Model

A number of classes have been imported from the Contact Tracing Model. Some of these are here for an example, so are not anything special:

* **ContactReader:** A class to import data from a CSV file
* **Output:** A CSV writer that outputs records from a map to a CSV file
* **RandomSingleton:** stores random number generators in a map
* **VirusStatus:** An enum for the current virus status.

* **ContactRecord:** A POJO that stores data from the CSV in Java
* **SeirRecord:** A POJO for storing people in different SEIR categories


## Logging
I have added the configuration for Log4J in the resources folder. All System.out lines should be replaced with logging. 
See RunModel.java line 24 and Populations line 64 for examples. 


## Running Pre-requisites
### Java SDK
Please ensure you have a Java SDK installed. I recommend Java 11 as it is the current LTS version.

### Gradle
To compile, run or test this model, Gradle must be installed. This manages the libraries utilised and simplifies the build process.

#### For MacOS:
I recommend using [homebrew](www.brew.sh). 

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


## Build/Test/Run Guide

To compile the project without running tests:
```shell script
gradle assemble
```

To compile and run the tests (there are none at present :-/):
```shell script
gradle build
```

To run the project:
```shell script
gradle run
```

## Version History


