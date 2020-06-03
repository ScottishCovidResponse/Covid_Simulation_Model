package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** SmallFamily: two adults of any age and one or two children */
public class SmallFamily extends Household {

    public SmallFamily(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        return Math.abs(adults - pensioners) < 2;
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
        return children < 2;
    }

    @Override
    public boolean pensionerRequired() {
        return Math.abs(adults - pensioners) < 2;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return false;
    }
}
