package uk.co.ramp.covid.simulation.place.householdtypes;

/** SingleParent: 1 adult of any age and any number of children */
public class SingleParent extends HouseholdType {
    @Override
    public boolean adultRequired() {
        return Math.abs(adults - pensioners) < 1;
    }

    @Override
    public boolean adultAllowed() {
        return false;
    }

    @Override
    public boolean childRequired() {
        return children < 1;
    }

    @Override
    public boolean childAllowed() {
        return true;
    }

    @Override
    public boolean pensionerRequired() {
        return Math.abs(adults - pensioners) < 1;
    }

    @Override
    public boolean pensionerAllowed() {
        return false;
    }
}
