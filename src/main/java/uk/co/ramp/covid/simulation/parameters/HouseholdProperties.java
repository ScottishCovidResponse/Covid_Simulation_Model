package uk.co.ramp.covid.simulation.parameters;

public class HouseholdProperties {
    public Double visitorLeaveRate = null;
    public Double neighbourVisitFreq = null;
    public Integer expectedNeighbours = null;

    public Double pGoShopping = null;
    public Double pGoRestaurant = null;
    public Integer householdIsolationPeriod = null;
    public Double pWillIsolate = null;
    public Double pLockCompliance = null;

    public boolean isValid() {
        return true;
        /*
        return PopulationParameters.isValidProbability(pGoShopping, "pGoShopping")
                && PopulationParameters.isValidProbability(pGoRestaurant, "pGoRestaurant")
                && PopulationParameters.isValidProbability(pWillIsolate, "pWillIsolate")
                && PopulationParameters.isValidProbability(pLockCompliance, "pLockCompliance");
    }
         */
    }
}
