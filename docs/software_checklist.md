# SCRC Software checklist

This checklist is part of ongoing work on a model scoresheet for SCRC models. It relates to software implementation, and assumes that other documents cover questions about model validation, data provenance and quality, quality of science, and policy readiness.

## Software Details

### Model / software name

> Covid_Simulation_Model

### Date

> 29/07/2020

### Version identifier

> 0.1.0-SNAPSHOT

## Overall statement

Do we have sufficient confidence in the correctness of the software to trust the results? Yes / Yes with caveats / No

This is your overall judgement on the level of confidence based on all the aspects of the checklist. There is no formulaic way to arrive at this overall assessment based on the individual checklist answers but please explain how the measures in place combine to reach this level of confidence and make clear any caveats (eg applies for certain ways of using the software and not others).

> - [ ] Yes
> - [x] Yes, with caveats
> - [ ] No
>
> Runs are repeatable. We have good tests. The code is clean. The documentation is a bit limited, but is probably sufficient. The data pipeline is not being used (and this checklist does not include a review of the input data).

## Checklist

Please use a statement from this list: "Sufficiently addressed", "Some work remaining or caveats", or "Needs to be addressed" to begin each response.

Additionally, for each question please explain the situation and include any relevant links (eg tool dashboards, documentation). The sub bullet points are to make the scope of the question clear and should be covered if relevant but do not have to be answered individually.

### Can a run be repeated and reproduce exactly the same results?

- How is stochasticity handled?
- Is sufficient meta-data logged to enable a run to be reproduced: Is the exact code version recorded (and whether the repository was "clean"), including versions of dependent libraries (e.g. an environment.yml file or similar) along with all command line arguments and the content of any configuration files? 
- Is there up-to-date documentation which explains precisely how to run the code to reproduce existing results? 

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> Results are reproducible.  Seeds can be specified up front or recorded. Git hash is logged (with ‘dirty’ flag).  Library versions are listed in build.gradle. Input parameters are copied to the output folder.

### Are there appropriate tests?  (And are they automated?)

- Are there unit tests? What is covered?
- System and integration tests?  Automated model validation tests?
- Regression tests? (Which show whether changes to the code lead to changes in the output. Changes to the model will be expected to change the output, but many other changes, such as refactoring and adding new features, should not. Having these tests gives confidence that the code hasn't developed bugs due to unintentional changes.)
- Is there CI?
- Is everything you need to run the tests (including documentation) in the repository (or the data pipeline where appropriate)?

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> We have good unit test coverage (around 90%).  We have simple automated tests which check that the model is behaving correctly, e.g. that some people (and not far too many or too few) have died.  These tests are run in CI.  Additionally, for releases, we also have an R script for producing graphs which can be manually reviewed.  We also have regression tests which are disabled by default, and temporarily enabled when we want to use them. (Almost every change we make changes the model results, and we want to avoid too many git merge conflicts.)

### Are the scientific results of runs robust to different ways of running the code?

- Running on a different machine?
- With different number of processes?
- With different compilers and optimisation levels?
- Running in debug mode?

(We don't require bitwise identical results here, but the broad conclusions after looking at the results of the test case should be the same.) 

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> This is straight-forward with Java.

### Has any sort of automated code checking been applied?

- For C++, this might just be the compiler output when run with "all warnings". It could also be more extensive static analysis. For other languages, it could be e.g. pylint, StaticLint.jl, etc.
- If there are possible issues reported by such a tool, have they all been either fixed or understood to not be important?

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> We are treating compiler warnings as errors, but javac does not give many useful warnings.  We often check (and fix) warnings in Eclipse and IDEA IntelliJ, which are more useful, but this is not checked by CI.  There is definitely room for improvement here, and we are now starting to use Codacy.com.  (The issues identified by Codacy have not been thoroughly reviewed and fixed, but they have been given a quick review, and it is identifying things which are “nice to haves” rather than things that need fixing.)

### Is the code clean, generally understandable and readable and written according to good software engineering principles?

- Is it modular?  Are the internal implementation details of one module hidden from other modules?
- Commented where necessary?
- Avoiding red flags such as very long functions, global variables, copy and pasted code, etc.?

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> There will always be room for improvement, and there are certainly still plenty of changes we would like to make here, but it is in a good state.

### Is there sufficient documentation?

- Is there a readme?
- Does the code have user documentation?
- Does the code have developer documentation?
- Does the code have algorithm documentation? e.g. something that describes how the model is actually simulated, or inference is performed?
- Is all the documentation up to date? 

> - [ ] Sufficiently addressed
> - [X] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> We have a simple readme, which explains how to build and run the model, together with additional documentation where it seemed particularly useful, such as for describing how the lockdown configuration works.  We could perhaps have more user level documentation, e.g. describing what the outputs look like.  We do not have separate developer or algorithm documentation, and do not plan to add any – preferring instead to put efforts into making the code easier to read, with comments where necessary, while avoiding an extra maintenance burden.  The documentation is up to date.

### Is there suitable collaboration infrastructure?

- Is the code in a version-controlled repository?
- Is there a license?
- Is an issue tracker used?
- Are there contribution guidelines?

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> GitHub is used for version control and issue tracking.  There is a licence file and basic contribution guidelines.

### Are software dependencies listed and of appropriate quality?

> - [X] Sufficiently addressed
> - [ ] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> Libraries are listed in build.gradle and of appropriate quality.

### Is input and output data handled carefully?

- Does the code use the data pipeline for all inputs and outputs?
- Is the code appropriately parameterized (i.e. have hard coded parameters been removed)?

> - [ ] Sufficiently addressed
> - [X] Some work remaining or caveats
> - [ ] Needs to be addressed
> 
> The code is not yet using the data pipeline. However, we have input data together with notes about its provenance in git, we record parameters used (and git hash, etc.) in the (unique) output folder, and we also have a set of output data in git.  It is appropriately parameterized.
