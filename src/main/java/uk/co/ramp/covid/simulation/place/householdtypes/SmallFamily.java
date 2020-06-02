package uk.co.ramp.covid.simulation.place.householdtypes;

/** SmallFamily: two adults of any age and one or two children */
public class SmallFamily extends HouseholdType {
    @Override
    public boolean adultRequired() {
        return Math.abs(adults - pensioners) < 2;
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
        return children < 2;
    }

    @Override
    public boolean pensionerRequired() {
        return Math.abs(adults - pensioners) < 2;
    }

    @Override
    public boolean pensionerAllowed() {
        return false;
    }
}
