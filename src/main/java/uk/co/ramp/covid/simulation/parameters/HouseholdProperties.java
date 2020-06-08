package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class HouseholdProperties {
    public Double visitorLeaveRate = null;
    public Double neighbourVisitFreq = null;
    public Integer expectedNeighbours = null;

    public Probability pGoShopping = null;
    public Probability pGoRestaurant = null;
    public Integer householdIsolationPeriod = null;
    public Probability pWillIsolate = null;
    public Probability pLockCompliance = null;
}
