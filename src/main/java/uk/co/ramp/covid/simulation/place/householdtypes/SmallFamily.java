package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;

/** SmallFamily: two adults of any age and one or two children */
public class SmallFamily extends Household {

    public SmallFamily() {
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
        return children < 1;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return children < 2;
    }

    @Override
    public boolean pensionerRequired() {
        return false;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return false;
    }

    @Override
    public boolean adultAnyAgeRequired() {
        return adults + pensioners < 2;
    }

    @Override
    public boolean additionalAdultAnyAgeAllowed() {
        return false;
    }

    @Override
    public NeighbourGroup getNeighbourGroup() {
        return NeighbourGroup.FAMILY;
    }
}
