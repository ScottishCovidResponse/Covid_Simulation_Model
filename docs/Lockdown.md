# Configuring Lockdown Parameters

The model allows experimentation with lockdown techniques, in particular, the
forcing of a full lockdown and subsequent easing. Lockdown parameters are
specified in the `model.json` parameter file - see
`example_model_params_lockdown` for the exact format.

# Lockdown Events

The main lockdown event is the `FullLockdown` which does the following:

- Non-key premises close. Those that are open increase social distance based on the `socialDistance` parameter
- Staff at key places who are set to furlough (currently only non-COVID hospital staff) stop going to work
- Regular Hospital appts decrease based on the `lockdownApptDecreasePercentage` parameter (in population parameters)
- People stop visiting restaurants and neighbours (if lockdown compliant)
- Shopping frequencies are decreased by `lockdownShoppingProbabilityAdjustment` (in population parameters)
- Households with a shielding member begin shielding (no working, shopping, restaurants or neighbour visits)
- Any external infection seeding from travel stops

We then support easing events to start adding in more interactions between the population:

- `ConstructionSiteEasing`: Sets `keyPremises` (probability) of sites as key (i.e. open) and reduces social distancing to `socialDistance`
- `OfficeEasing`: Sets `keyPremises` (probability) of offices as key (i.e. open) and reduces social distancing to `socialDistance`
- `NurseryEasing`: Sets `keyPremises` (probability) of nurseries as key (i.e. open) and reduces social distancing to `socialDistance`
- `RestaurantEasing`: Sets `keyPremises` (probability) of restaurants as key (i.e. open), reduces social distancing to `socialDistance`, and adjusts the visit frequency by `visitFrequencyAdjustment`
- `ShopEasing`: Sets `keyPremises` (probability) of shops as key (i.e. open), reduces social distancing to `socialDistance`, and adjusts the visit frequency by `visitFrequencyAdjustment`
- `SchoolEasing`: Sets `keyPremises` (probability) of schools as key (i.e. open), reduces social distancing to `socialDistance`, and allows `pAttendsSchool` children to return (note each time it's called you might get a different set of children as we resample all their statuses).
- `TravelEasing`: Starts external seeding to model travelling to the `pTravelSeed` daily infection probability
- `ShieldingEasing`: Stops households from shielding. If `partial` is true, then
  `partialShieldProbability` of households enter partial shielding mode where
  they can visit other households (still avoiding work etc)
- `FullLockdownEasing`: Reverts the changes made by a `FullLockdown` event

# Dynamic Events

To allow lockdowns to be introduced based on the current state of the model, we also support LockdownEventGenerators. Currently we only have the `LocalLockdownEventGenerator` which does the following:

- If the number of new cases in a day is greater than `newCasesThreshold` *or* the number hospitalised in a day is greater than `numHospitalisedThreshold` then generate a `FullLockdownEvent`

# Creating New Events

While we support a limited range of events, it's design to be as easy as possible to ad new events/generators.

Lockdown events are found in the `uk.co.ramp.covid.simulation.lockdown` and `uk.co.ramp.covid.simulation.lockdown.easing` packages. New events should inherit from `LockdownEvent` and override the `apply()` method to specify what should happen to the population when the event occurs.

To make the event settable from the parameters file, you must also add the component to the maps within `LockdownTypeMaps.java` which registers it with the parser.

## Creating a Generator

Similarly, lockdown event generators are in the `uk.co.ramp.covid.simulation.lockdown` packages. New generator should inherit from `LockdownEventGenerator` and override the `generateEvents(Time now)` method. Unlike events, this method is called daily (after `startDay`) allowing control based on the population state over time. Event generators should *never* directly perform lockdowns, and instead generate a list of new `LockdownEvent` objects. This approach allows a generator to, for example, both perform a full lockdown and schedule the release one week later.

To make the generator settable from the parameters file, you must also add the component to the maps within `LockdownTypeMaps.java` which registers it with the parser.
