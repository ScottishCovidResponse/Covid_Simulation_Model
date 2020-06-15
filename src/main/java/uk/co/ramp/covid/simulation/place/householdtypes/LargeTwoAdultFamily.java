package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;

/** LargeTwoAdultFamily: two adults of any age and three or more children */
public class LargeTwoAdultFamily extends Household {
    
    public LargeTwoAdultFamily() {
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
        return children < 3;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return true;
    }

    @Override
    public boolean pensionerRequired() {
       return false;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return additionalAdultsAllowed();
    }

    @Override
    public boolean adultAnyAgeRequired() {
        return adults + pensioners < 2;
    }

    @Override
    public boolean additionalAdultAnyAgeAllowed() {
        return false;
    }
}
