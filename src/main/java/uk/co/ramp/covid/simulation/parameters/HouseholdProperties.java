package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class HouseholdProperties {
    /** Hourly probability visitors (including families) leave */
    public Probability pVisitorsLeaveHouseholdHour = null;

    /** Power-law base variable used to eventually determine the probability of visiting a neighbour daily */
    public Double householdVisitsNeighbourDaily = null;

    /** Poisson distributed expected number of neighbours each household has */
    public Integer expectedNeighbours = null;

    /** Allows pensioners to meet more pensioners etc */
    public Probability pNeighbourFromSameGroup = null;
    public Probability pNeighbourFromOtherGroup = null;

    /** Hourly probability a family goes shopping */
    public Probability pGoShoppingHour = null;
    /** Reduction in shopping probability during lockdown */
    public Double lockdownShoppingProbabilityAdjustment  = null;

    /** Hourly probability a family goes to a restaurant */
    public Probability pGoRestaurantHour = null;

    /** Number of days a household with a positive test isolates for; Resets as new cases occur in the household */
    public Integer householdIsolationPeriodDays = null;

    /** Probability isolation/lockdown is complied with */
    public Probability pWillIsolate = null;
    public Probability pLockCompliance = null;

    /** Time window where neighbour visits are allowed */
    public Integer neighbourOpeningTime = null;
    public Integer neighbourClosingTime = null;

    /** Minimum age (inclusive) someone in the household must be to attemot to enter shielding */
    public Integer minShieldingAge = null;
    /** Probability a household enters shielding during lockdown. If a member is > shieldingAge */
    public Probability pEntersShielding = null;
    
    public boolean isValid() {
        return 2*pNeighbourFromOtherGroup.asDouble() + pNeighbourFromSameGroup.asDouble() == 1.0
                && expectedNeighbours >= 0
                && neighbourOpeningTime >= 0 && neighbourOpeningTime < 24
                && neighbourClosingTime >= 0 && neighbourClosingTime < 24
                && neighbourOpeningTime < neighbourClosingTime
                && minShieldingAge >= 0 && minShieldingAge <= 100;
    }
}
