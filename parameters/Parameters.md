Simulation model parameters can be tweaked by creating a
`parameters.json` file with the parameters specified below. In the case
you only wish to vary a subset of the parameters, then any missing
parameters are given the specified default value.

An example parameters file is in `example_params.json`

Disease Parameters
==================

Determines parameters of the (COVID) disease

-   Object name: `diseaseParameters`

| Parameter Name             | Description                                                    | Type   | Default Value |
|----------------------------|----------------------------------------------------------------|--------|---------------|
| meanLatentPeriod           | Expected (Poisson) disease latent period                       | Int    | 7             |
| meanAsymptomaticPeriod     | Expected (Poisson) disease asymptomatic period                 | Int    | 1             |
| meanPhase1DurationMild     | Expected (Poisson) disease phase 1 duration (mild symptoms)    | Int    | 5             |
| meanPhase1DurationSeverse  | Expected (Poisson) disease phase 1 duration (severse symptoms) | Int    | 10            |
| mortalityRate              | Daily probability of death (Phase 2)                           | Double | 0.01          |
| childProgressionPhase2     | Daily probability a child progresses to Phase 2                | Double | 0.02          |
| adultProgressionPhase2     | Daily probability an adult progresses to Phase 2               | Double | 0.15          |
| pensionerProgressionPhase2 | Daily probability a pensioner progresses to Phase 2            | Double | 0.8           |

Population Parameters
=====================

Determines the distribution of person types in the population. Total of
all parameters should be 1.

-   Object name: `population`

| Parameter Name | Description                         | Type     | Default Value |
|----------------|-------------------------------------|----------|---------------|
| pInfants       | Probability a person is an infant   | Double   | 0.08          |
| pChildren      | Probability a person is a child     | Double   | 0.2           |
| pAdults        | Probability a person is an adult    | d Double | 0.5           |
| pPensioners    | Probability a person is a pensioner | Double   | 0.22          |

Person Properties
=================

Determines basic properties of people

-   Object name: `personProperties`

| Parameter Name | Description                            | Type   | Default Value |
|----------------|----------------------------------------|--------|---------------|
| pTransmission  | Base transmission rate for a person    | Double | 0.45          |
| pQuarantine    | Probability a person respects lockdown | Double | 0.9           |

Worker Allocations
==================

Determines the probability of an *adult* working in a particular area.
Distribution should add to 1.

-   Object name: `workerAllocation`

| Parameter Name | Description                            | Type   | Default Value |
|----------------|----------------------------------------|--------|---------------|
| pOffice        | Probability of working in an office    | Double | 0.2           |
| pShop          | Probability of working in a shop       | Double | 0.1           |
| pHospital      | Probability of working in a hospital   | Double | 0.1           |
| pConstruction  | Probability of working in construction | Double | 0.1           |
| pTeacher       | Probability of being a teacher         | Double | 0.2           |
| pRestaurant    | Probability of working in a restaurant | 0.1    |               |
| pUnemployed    | Probability of being unemployed        | Double | 0.2           |

Infant Properties
=================

Determines if an infant should go to nursery or not

-   Object name: `infantAllocation`

| Parameter Name  | Description                             | Type   | Default Value |
|-----------------|-----------------------------------------|--------|---------------|
| pAttendsNursery | Probability an infant goes to a nursery | Double | 0.5           |

Household Parameters
====================

Determines the distribution of household types, e.g. adult and child
households. Total of all probabilities should be 1. Note: Child
households may include either/both children or infants.

-   Object name: `households`

| Parameter Name          | Description                                       | Type   | Default Value |
|-------------------------|---------------------------------------------------|--------|---------------|
| pAdultOnly              | Probability of an adult only household            | Double | 0.3           |
| pPensionerOnly          | Probability of a pensioner only household         | Double | 0.1           |
| pPensionerAdult         | Probability of an adult/pensioner household       | Double | 0.1           |
| AdultChildren           | Probability of an adult/child household           | Double | 0.3           |
| pPnsionerChildren       | Probability of an pensioner/child household       | Double | 0.1           |
| pAdultPensionerChildren | Probability of an adult/pensioner/child household | Double | 0.1           |

Household Size Distributions
============================

Determines the probability of adding a person of a particular type to a
household of a given size. E.g. for adults. 1:0.8, says there is an 80%
chance that an additional adult will be added to the household.

Note that househoolds are pre-allocated to meet their type requirements,
e.g. an adult only household will contain at least one adult, so these
distributions only need to account for household sizes greater than 1.

-   Object name: `additionalMembersDistributions`

| Parameter Name          | Description                                                    | Type                              | Default Value                           |
|-------------------------|----------------------------------------------------------------|-----------------------------------|-----------------------------------------|
| adultAllocationPMap     | Probability map for adding additional adults to households     | Map (householdSize : probability) | (1:0.8),(2:0.5),(3:0.3),(4:0.2),(5:0.1) |
| pensionerAllocationPMap | Probability map for adding additional pensioners to households | Map (householdSize : probability) | (1:0.8),(2:0.5),(3:0.3),(4:0.2),(5:0.1) |
| childAllocationPMap     | Probability map for adding additional children to households   | Map (householdSize : probability) | (1:0.8),(2:0.5),(3:0.3),(4:0.2),(5:0.1) |
| infantAllocationPMap    | Probability map for adding additional infants to households    | Map (householdSize : probability) | (1:0.8),(2:0.5),(3:0.3),(4:0.2),(5:0.1) |

Neighbour Properties
====================

Determines the number and expected visit rate of neighbours

-   Object name: `neighbourProperties`

| Parameter Name     | Description                              | Type   | Default Value |
|--------------------|------------------------------------------|--------|---------------|
| visitFrequency     | Probability a neighbour visits in a hour | Double | 0.006         |
| expectedNeighbours | Expected (Poisson) number of neighbours  | Int    | 3             |

Building Distributions
======================

Determines the number of buildings, of a particular type, per N people.

-   Object name: `buildingDistribution`

| Parameter Name    | Description                     | Type | Default Value |
|-------------------|---------------------------------|------|---------------|
| hospitals         | Hospitals per N people          | Int  | 10000         |
| schools           | Schools per N people            | Int  | 2000          |
| shops             | Shops per N people              | Int  | 500           |
| offices           | Offices per N people            | Int  | 250           |
| constructionSites | Construction Sites per N people | Int  | 1000          |
| nurseries         | Nurseries per N people          | Int  | 2000          |
| restaurants       | Restaurants per N people        | Int  | 1000          |

Building Properties
===================

Determines the transmission properties of particular buildings, and
whether a particular building must close during a lockdown (i.e. is it
an essential service).

-   Object name: `buildingProperties`

| Parameter Name         | Description                                             | Type   | Default Value |
|------------------------|---------------------------------------------------------|--------|---------------|
| pBaseTrans             | Base disease transmission probability for all places    | Double | 0.03          |
| pHospitalTrans         | Disease transmission probability in a hospital          | Double | 0.03          |
| pConstructionSiteTrans | Disease transmission probability on a construction site | Double | 0.2           |
| pNurseryTrans          | Disease transmission probability in a nursery           | Double | 0.044118      |
| pOfficeTrans           | Disease transmission probability in an office           | Double | 0.4           |
| pRestaurantTrans       | Disease transmission probability in a restaurant        | Double | 1             |
| pSchoolTrans           | Disease transmission probability in a school            | Double | 0.044118      |
| pShopTrans             | Disease transmission probability in a shop              | Double | 0.2           |
| pHospitalKey           | Probability a hospital closes in a lockdown             | Double | 0             |
| pConstructionSiteKey   | Probability a construction site closes in a lockdown    | Double | 0.5           |
| pOfficeKey             | Probability an office closes in a lockdown              | Double | 0.5           |
| pShopKey               | Probability a shop closes in a lockdown                 | Double | 0.5           |
