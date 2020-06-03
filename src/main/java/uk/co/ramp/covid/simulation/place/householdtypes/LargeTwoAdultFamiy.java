package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** LargeTwoAdultFamily: two adults of any age and three or more children */
public class LargeTwoAdultFamiy extends Household {
    

    public LargeTwoAdultFamiy(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
            return adults + pensioners < 2;
    }

    @Override
    public boolean additionalAdultsAllowed() {
            return false;
    }

    @Override
    public boolean childRequired() {
        return children < 3;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return true;
    }

    @Override
    public boolean pensionerRequired() {
       return adultRequired();
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return additionalAdultsAllowed();
    }
}
