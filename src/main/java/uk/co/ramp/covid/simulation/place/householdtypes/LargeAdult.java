package uk.co.ramp.covid.simulation.place.householdtypes;

/** LargeAdult: Three or more adults (any age) and no children */
public class LargeAdult extends HouseholdType {
    @Override
    public boolean adultRequired() {
        return adults + pensioners < 3;
    }

    @Override
    public boolean adultAllowed() {
        return true;
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
        return adults + pensioners < 3;
    }

    @Override
    public boolean pensionerAllowed() {
        return true;
    }
}
