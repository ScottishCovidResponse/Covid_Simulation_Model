package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.util.RNG;

/** AdultPensioner: one adult aged 16-64 and one of pensionable age and no children */

public class AdultPensioner extends Household {
    public AdultPensioner(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return adults < 1;
    }

    @Override
    public boolean additionalAdultsAllowed() {
        return false;
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
        return pensioners < 1;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return false;
    }
}
