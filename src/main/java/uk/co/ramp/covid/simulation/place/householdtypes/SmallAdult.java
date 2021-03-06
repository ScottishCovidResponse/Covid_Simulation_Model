package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;

/** SmallAdult: Two non-pensioner adults and no children */
public class SmallAdult extends Household {

    public SmallAdult() {
        super();
    }

    @Override
    public boolean adultRequired() {
        return adults < 2;
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
        return false;
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

    @Override
    public NeighbourGroup getNeighbourGroup() {
        return NeighbourGroup.ADULT;
    }
}
