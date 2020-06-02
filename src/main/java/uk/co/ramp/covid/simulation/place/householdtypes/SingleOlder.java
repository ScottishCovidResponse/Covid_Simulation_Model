package uk.co.ramp.covid.simulation.place.householdtypes;

/** SingleOlder: One pensioner */
public class SingleOlder extends HouseholdType {
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
