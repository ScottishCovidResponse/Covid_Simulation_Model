Simulation model parameters can be tweaked by creating a
`parameters.json` file with the parameters specified below.

The parameters fall into 2 high level categories `disease` or `population`.

An example parameters file is in `example_params.json`

# Disease

## Disease Parameters

Determines parameters of the (COVID) disease

-   Object name: `diseaseParameters`

| Parameter Name             | Description                                                    | Type   |
|----------------------------|----------------------------------------------------------------|--------|
| meanLatentPeriod           | Expected (Poisson) disease latent period                       | Int    |
| meanAsymptomaticPeriod     | Expected (Poisson) disease asymptomatic period                 | Int    |
| meanPhase1DurationMild     | Expected (Poisson) disease phase 1 duration (mild symptoms)    | Int    |
| meanPhase1DurationSeverse  | Expected (Poisson) disease phase 1 duration (severse symptoms) | Int    |
| mortalityRate              | Daily probability of death (Phase 2)                           | Double |
| childProgressionPhase2     | Daily probability a child progresses to Phase 2                | Double |
| adultProgressionPhase2     | Daily probability an adult progresses to Phase 2               | Double |
| pensionerProgressionPhase2 | Daily probability a pensioner progresses to Phase 2            | Double |

# Population
## Population Parameters

Determines the distribution of person types in the population. Total of
all parameters should be 1.

-   Object name: `population`

| Parameter Name | Description                         | Type     | 
|----------------|-------------------------------------|----------|
| pInfants       | Probability a person is an infant   | Double   | 
| pChildren      | Probability a person is a child     | Double   | 
| pAdults        | Probability a person is an adult    | Double   | 
| pPensioners    | Probability a person is a pensioner | Double   | 

## Person Properties

Determines basic properties of people

-   Object name: `personProperties`

| Parameter Name | Description                            | Type   | 
|----------------|----------------------------------------|--------|
| pTransmission  | Base transmission rate for a person    | Double | 
| pQuarantine    | Probability a person respects lockdown | Double | 

## Worker Allocations

Determines the probability of an *adult* working in a particular area.
Distribution should add to 1.

-   Object name: `workerAllocation`

| Parameter Name | Description                            | Type   |
|----------------|----------------------------------------|--------|
| pOffice        | Probability of working in an office    | Double |
| pShop          | Probability of working in a shop       | Double |
| pHospital      | Probability of working in a hospital   | Double |
| pConstruction  | Probability of working in construction | Double |
| pTeacher       | Probability of being a teacher         | Double |
| pRestaurant    | Probability of working in a restaurant | Double |
| pUnemployed    | Probability of being unemployed        | Double |

## Infant Properties

Determines if an infant should go to nursery or not

-   Object name: `infantAllocation`

| Parameter Name  | Description                             | Type   |
|-----------------|-----------------------------------------|--------|
| pAttendsNursery | Probability an infant goes to a nursery | Double |

## Household Parameters

Determines the distribution of household types, e.g. adult and child
households. Total of all probabilities should be 1. Note: Child
households may include either/both children or infants.

-   Object name: `households`

| Parameter Name          | Description                                       | Type   |
|-------------------------|---------------------------------------------------|--------|
| pAdultOnly              | Probability of an adult only household            | Double |
| pPensionerOnly          | Probability of a pensioner only household         | Double |
| pPensionerAdult         | Probability of an adult/pensioner household       | Double |
| AdultChildren           | Probability of an adult/child household           | Double |
| pPnsionerChildren       | Probability of an pensioner/child household       | Double |
| pAdultPensionerChildren | Probability of an adult/pensioner/child household | Double |

## Household Size Distributions

Determines the probability of adding a person of a particular type to a
household of a given size. E.g. for adults. 1:0.8, says there is an 80%
chance that an additional adult will be added to the household.

Note that househoolds are pre-allocated to meet their type requirements,
e.g. an adult only household will contain at least one adult, so these
distributions only need to account for household sizes greater than 1.

-   Object name: `additionalMembersDistributions`

| Parameter Name          | Description                                                    | Type                              |
|-------------------------|----------------------------------------------------------------|-----------------------------------|
| adultAllocationPMap     | Probability map for adding additional adults to households     | Map (householdSize : probability) |
| pensionerAllocationPMap | Probability map for adding additional pensioners to households | Map (householdSize : probability) |
| childAllocationPMap     | Probability map for adding additional children to households   | Map (householdSize : probability) |
| infantAllocationPMap    | Probability map for adding additional infants to households    | Map (householdSize : probability) |

## Neighbour Properties

Determines the number and expected visit rate of neighbours

-   Object name: `neighbourProperties`

| Parameter Name     | Description                              | Type   |
|--------------------|------------------------------------------|--------|
| visitFrequency     | Probability a neighbour visits in a hour | Double |
| expectedNeighbours | Expected (Poisson) number of neighbours  | Int    |

## Building Distributions

Determines the number of buildings, of a particular type, per N people.

-   Object name: `buildingDistribution`

| Parameter Name    | Description                     | Type |
|-------------------|---------------------------------|------|
| hospitals         | Hospitals per N people          | Int  |
| schools           | Schools per N people            | Int  |
| shops             | Shops per N people              | Int  |
| offices           | Offices per N people            | Int  |
| constructionSites | Construction Sites per N people | Int  |
| nurseries         | Nurseries per N people          | Int  |
| restaurants       | Restaurants per N people        | Int  |

## Building Properties

Determines the transmission properties of particular buildings, and
whether a particular building must close during a lockdown (i.e. is it
an essential service).

-   Object name: `buildingProperties`

| Parameter Name         | Description                                             | Type   |
|------------------------|---------------------------------------------------------|--------|
| pBaseTrans             | Base disease transmission probability for all places    | Double |
| pHospitalTrans         | Disease transmission probability in a hospital          | Double |
| pConstructionSiteTrans | Disease transmission probability on a construction site | Double |
| pNurseryTrans          | Disease transmission probability in a nursery           | Double |
| pOfficeTrans           | Disease transmission probability in an office           | Double |
| pRestaurantTrans       | Disease transmission probability in a restaurant        | Double |
| pSchoolTrans           | Disease transmission probability in a school            | Double |
| pShopTrans             | Disease transmission probability in a shop              | Double |
| pHospitalKey           | Probability a hospital closes in a lockdown             | Double |
| pConstructionSiteKey   | Probability a construction site closes in a lockdown    | Double |
| pOfficeKey             | Probability an office closes in a lockdown              | Double |
| pShopKey               | Probability a shop closes in a lockdown                 | Double |
