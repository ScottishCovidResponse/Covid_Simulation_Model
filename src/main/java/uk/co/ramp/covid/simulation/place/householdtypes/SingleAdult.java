package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** SingleAdult: A single non-pensioner Adult */
public class SingleAdult extends Household {

    public SingleAdult(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return adults < 1;
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
