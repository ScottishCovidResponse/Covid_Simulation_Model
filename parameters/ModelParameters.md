Model parameters can be tweaked by creating a `model.json` with the parameters
below.

An example parameters file is in `example_model_params.json`

| Parameter Name | Description                                      | Type         |
|----------------|--------------------------------------------------|--------------|
| populationSize | Number of people in the population               | Int          |
| nInfections    | Number of initial infections                     | Int          |
| nDays          | Number of days to simulate                       | Int          |
| nIters         | Number of simulation iterations                  | Int          |
| outputFile     | Name/location of output csv file                 | String       |
| rngSeed        | Seed for random number generation                | Int          |

## Lockdowns 

Lockdowns can be simulated by optionally including either `lockDown` or
`schoollockDown` objects with the following parameters

| Parameter Name | Description                        | Type   |
|----------------|------------------------------------|--------|
| startDay       | Day the lockdown begins            | Int    |
| endDay         | Day the lockdown ends              | Int    |
| socialDistance | Probability the lockdown is obeyed | Double |
