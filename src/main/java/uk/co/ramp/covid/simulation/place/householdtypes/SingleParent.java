package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** SingleParent: 1 adult of any age and any number of children */
public class SingleParent extends Household {

    public SingleParent(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return Math.abs(adults - pensioners) < 1;
    }

    @Override
    public boolean additionalAdultsAllowed() {
        return false;
    }

    @Override
    public boolean childRequired() {
        return children < 1;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return true;
    }

    @Override
    public boolean pensionerRequired() {
        return Math.abs(adults - pensioners) < 1;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return false;
    }
}
