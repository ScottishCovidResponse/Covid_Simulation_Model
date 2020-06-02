package uk.co.ramp.covid.simulation.place.householdtypes;

/** SmallAdult: Two non-pensioner adults and no children */
public class SmallAdult extends HouseholdType {
    @Override
    public boolean adultRequired() {
        return adults < 2;
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
