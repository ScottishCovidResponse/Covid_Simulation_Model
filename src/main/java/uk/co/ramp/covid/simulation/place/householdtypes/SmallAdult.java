package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** SmallAdult: Two non-pensioner adults and no children */
public class SmallAdult extends Household {

    public SmallAdult(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return adults < 2;
    }

    @Override
    public boolean adultAllowed() {
        return false;
    }

    @Override
    public boolean childRequired() {
        return false;
    }

    @Override
    public boolean childAllowed() {
        return false;
    }

    @Override
    public boolean pensionerRequired() {
        return false;
    }

    @Override
    public boolean pensionerAllowed() {
        return false;
    }
}
