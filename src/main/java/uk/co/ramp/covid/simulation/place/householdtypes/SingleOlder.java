package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** SingleOlder: One pensioner */
public class SingleOlder extends Household {

    public SingleOlder(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return false;
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
        return pensioners < 1;
    }

    @Override
    public boolean pensionerAllowed() {
        return false;
    }
}
