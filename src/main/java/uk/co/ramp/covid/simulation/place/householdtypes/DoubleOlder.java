package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;

/** DoublePensioner: two adults of pensionable age and no children */
public class DoubleOlder extends Household {
    
    public DoubleOlder() {
        super();
    }

    @Override
    public boolean adultRequired() {
        return false;
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
        return pensioners < 2;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return false;
    }

    @Override
    public boolean adultAnyAgeRequired() {
        return false;
    }

    @Override
    public boolean additionalAdultAnyAgeAllowed() {
        return false;
    }
}
