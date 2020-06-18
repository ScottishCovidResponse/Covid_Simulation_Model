package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class HouseholdProperties {
    public Probability pVisitorsLeaveHousehold = null;
    public Double householdVisitsNeighbourDaily = null;

    public Integer expectedNeighbours = null;
    public Probability pNeighbourFromSameGroup = null;
    public Probability pNeighbourFromOtherGroup = null;

    public Probability pGoShopping = null;
    public Probability pGoRestaurant = null;
    public Integer householdIsolationPeriod = null;
    public Probability pWillIsolate = null;
    public Probability pLockCompliance = null;
    
    public Integer neighbourOpeningTime = null;
    public Integer neighbourClosingTime = null;
    
    public boolean isValid() {
        return 2*pNeighbourFromOtherGroup.asDouble() + pNeighbourFromSameGroup.asDouble() == 1.0;
    }
}
