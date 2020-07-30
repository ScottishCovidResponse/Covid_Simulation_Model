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

5.  Update the software checklist.

6.  Run the model using `example_model_params_lothian.json` and `example_population_params.json`, and then replace `RCode\CovidSimAnalysis\exampleData\current\out.csv` with the new results.

7.  Use `RCode\CovidSimAnalysis\scripts\OutputPlotCode.R` to generate a new set of graphs, which should then also be committed to git (note that certain libraries may be required).

8.  Check the results.
Results of a model run are processed and plotted using the given R scripts. Whilst there is no formal analysis of the model fit relative to the data there are certain plots to check based on epidemiological evidence.
    - GenerationTime.jpg. The daily generation time should be between 9 and 10 with reasonably tight confidence intervals. There is a dip in the generation time before lockdown (day 55), this is an expected effect of lockdown
    - CumulativeR.jpg and DailyR.jpg. Pre-lockdown we expect R to be between 2.5 and 3 and the confidence intervals should narrow as the burden of infection increases. Post-lockdown the daily R should dip below 1, but then increase to but largely remaining slightly under 1. There is a fluctuation in the dialy R with lower values at the weekend. This is expected.
    -  DeathCumPlot.jpg. The cumulative number of deaths is plotted against the (Lothian) health board total (points). The data should remain within the 90% CIs of the model outputs
    - DeathLocation.jpg. Modelled location of deaths plotted against the location of deaths according to NRS data. It is noted that deaths at hospital and deaths at home both fall slightly outside the 90% CIs of the model runs, but could be easily fixed
    - DeathsAge.jpg. Whilst the variation in deaths by age remains high, the median line should remain around 80 years.
    - SiteOfInfectionBarplotAll.jpg and SiteOfInfectionBArplotPreLock.jpg. Post lockdown the proportion of infections in schools and restaurants should tail off markedly.

9.  If something is wrong, then create a new release candidate branch (e.g. '-rc2'), fix the problems and then return to step 3 (above), rerunning the model and rechecking anything that has changed.

10. Remove the 'rc' suffix from the branch, e.g. renaming it to "release-v1.0.0".

11. Update the version number in `build.gradle`.

12. Create a pull request against the `master` branch.

13. After the PR has been reviewed and merged, then add "-SNAPSHOT" to the version number in `build.gradle`, then make a PR against `develop`.

14. Use the GitHub web interface to tag the release, give it a title (e.g. "v1.0.0") and description (e.g. a description of new features), and upload a .zip file.  See <https://docs.github.com/en/github/administering-a-repository/managing-releases-in-a-repository> .

15. Download the .zip file, and check that it works.
