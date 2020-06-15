package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** LargeAdult: Three or more adults (any age) and no children */
public class LargeAdult extends Household {

    public LargeAdult() {
        super();
    }

    @Override
    public boolean adultRequired() {
        return false;
    }

    @Override
    public boolean additionalAdultsAllowed() {
        return true;
    }

    @Override
    public boolean childRequired() {
        return false;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return false;
    }

    @Override
    public boolean pensionerRequired() { return false; }

    @Override
    public boolean additionalPensionersAllowed() {
        return true;
    }

    @Override
    public boolean adultAnyAgeRequired() {
        return adults + pensioners < 3;
    }

    @Override
    public boolean additionalAdultAnyAgeAllowed() {
        return true;
    }
}
