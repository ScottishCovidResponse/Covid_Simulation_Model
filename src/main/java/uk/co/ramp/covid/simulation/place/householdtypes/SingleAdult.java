package uk.co.ramp.covid.simulation.place.householdtypes;

/** SingleAdult: A single non-pensioner Adult */
public class SingleAdult extends HouseholdType {
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
