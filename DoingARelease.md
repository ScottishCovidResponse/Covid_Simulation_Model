# Doing a release of Covid_Simulation_Model

1. Use SemVer rules (<https://semver.org/>) to pick the next release number.  (The "API" for this software is defined by its command line options together with its input and output file formats.)

2.  Ensure that CI has passed on the latest commit on the develop branch.

3.  Create a release candidate branch, e.g.
```sh
    git checkout develop
    git checkout -b release-v1.0.0-rc1
```

4.  Consider checking:

    - The build succeeds and tests pass.
    - The documentation is up-to-date.
    - Any warnings in IntelliJ and/or Eclipse.
    - Codacy warnings.
    - Any mention of "TODO" or "fudge" in the code.
    - Tests marked "@Ignore".
    - If any parameters have been hard coded (when they should be explicitly included in inputs).

5.  Update the software checklist (once we have this - **TODO: Add this when it is ready for use.**)

6.  Run the model using `example_model_params_lothian.json` and `example_population_params.json`, and then replace `RCode\CovidSimAnalysis\exampledata\current\out.csv` with the new results.

7.  Use `RCode\CovidSimAnalysis\scripts\OutputPlotCode.R` to generate a new set of graphs, which should then also be committed to git.

8.  Check the results. **TODO: Explain what to check here.**

9.  If something is wrong, then create a new release candidate branch (e.g. '-rc2'), fix the problems and then return to step 3 (above), rerunning the model and rechecking anything that has changed.

10. Remove the 'rc' suffix from the branch, e.g. renaming it to "release-v1.0.0".

11. Update the version number in `build.gradle`.

12. Create a pull request against the `master` branch.

13. After the PR has been reviewed and merged, then add "-SNAPSHOT" to the version number in `build.gradle`, then make a PR against `develop`.

14. Use the GitHub web interface to tag the release, give it a title (e.g. "v1.0.0") and description (e.g. a description of new features), and upload a .jar file.  See <https://docs.github.com/en/github/administering-a-repository/managing-releases-in-a-repository> .
