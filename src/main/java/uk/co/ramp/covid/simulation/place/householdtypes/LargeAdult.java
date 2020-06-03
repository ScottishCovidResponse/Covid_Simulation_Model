package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** LargeAdult: Three or more adults (any age) and no children */
public class LargeAdult extends Household {

    public LargeAdult(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return adults + pensioners < 3;
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
    public boolean pensionerRequired() {
        return adults + pensioners < 3;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return true;
    }
}
