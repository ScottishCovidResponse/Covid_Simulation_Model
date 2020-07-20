# Doing a release of Covid_Simulation_Model

0. Use SemVer rules (https://semver.org/) to pick the next release number.  (The "API" for this software is defined by its command line options together with its input and output file formats.)

1. Create a release branch, e.g.

```
    git checkout develop
    git checkout -b release-v1.0.0
```

2. Consider checking:

   - If the documentation is up-to-date.
   - Any warnings in IntelliJ and/or Eclipse.
   - Codacy warnings.
   - Any mention of "TODO" or "fudge" in the code.
   - Tests marked "@Ignore".
   - If any parameters have been hard coded (when they should be explicitly included in inputs).

1. **TODO: Add a step here to update the software checklist, when it is ready for use.**

1. Run the model using `example_model_params_lothian.json` and `example_population_params.json`, and then replace `RCode\CovidSimAnalysis\exampledata\current\out.csv` with the new results.

1. Use `RCode\CovidSimAnalysis\scripts\OutputPlotCode.R` to generate a new set of graphs, which should then also be committed to git.

1. Check the results. **TODO: Explain what to check here.**

1. If something is wrong, then fix it and return to step 2 (above) - rerunning the model and rechecking anything that has changed.

1. Update the version number in `build.gradle`.

1. Create a pull request against the `master` branch.

1. After the PR has been reviewed and merged, then add "-SNAPSHOT" to the version number in `build.gradle`, then make a PR against `develop`.

1. Use the GitHub web interface to tag the release, give it a title and description, and upload a .jar file.  See https://docs.github.com/en/github/administering-a-repository/managing-releases-in-a-repository . **TODO: Clarify what to do here.**

1. **TODO: Add process for sharing the release (.jar file) on the CSD3 HPC system.**