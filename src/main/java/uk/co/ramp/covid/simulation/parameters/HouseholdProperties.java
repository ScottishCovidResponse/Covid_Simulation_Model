package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HouseholdProperties that = (HouseholdProperties) o;
        return Objects.equals(pVisitorsLeaveHouseholdHour, that.pVisitorsLeaveHouseholdHour) &&
                Objects.equals(householdVisitsNeighbourDaily, that.householdVisitsNeighbourDaily) &&
                Objects.equals(expectedNeighbours, that.expectedNeighbours) &&
                Objects.equals(pNeighbourFromSameGroup, that.pNeighbourFromSameGroup) &&
                Objects.equals(pNeighbourFromOtherGroup, that.pNeighbourFromOtherGroup) &&
                Objects.equals(pGoShoppingHour, that.pGoShoppingHour) &&
                Objects.equals(lockdownShoppingProbabilityAdjustment, that.lockdownShoppingProbabilityAdjustment) &&
                Objects.equals(pGoRestaurantHour, that.pGoRestaurantHour) &&
                Objects.equals(householdIsolationPeriodDays, that.householdIsolationPeriodDays) &&
                Objects.equals(pWillIsolate, that.pWillIsolate) &&
                Objects.equals(pLockCompliance, that.pLockCompliance) &&
                Objects.equals(neighbourOpeningTime, that.neighbourOpeningTime) &&
                Objects.equals(neighbourClosingTime, that.neighbourClosingTime) &&
                Objects.equals(minShieldingAge, that.minShieldingAge) &&
                Objects.equals(pEntersShielding, that.pEntersShielding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pVisitorsLeaveHouseholdHour, householdVisitsNeighbourDaily, expectedNeighbours, pNeighbourFromSameGroup, pNeighbourFromOtherGroup, pGoShoppingHour, lockdownShoppingProbabilityAdjustment, pGoRestaurantHour, householdIsolationPeriodDays, pWillIsolate, pLockCompliance, neighbourOpeningTime, neighbourClosingTime, minShieldingAge, pEntersShielding);
    }
}
