package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.util.RNG;

/** LargeFamily: two adults of any age and three or more children,
 *  or three or more adults of any age and one or more children */
public class LargeFamily extends HouseholdType {

    private boolean twoAdult = RNG.get().nextUniform(0,1) < 0.5;

    @Override
    public boolean adultRequired() {
        if (twoAdult) {
            return adults + pensioners < 2;
        }
        return adults + pensioners < 3;
    }

    @Override
    public boolean adultAllowed() {
        if (twoAdult) {
            return false;
        }
        return true;
    }

    @Override
    public boolean childRequired() {
        if (twoAdult) {
            return children < 3;
        }

        return children < 1;
    }

    @Override
    public boolean childAllowed() {
        return true;
    }

    @Override
    public boolean pensionerRequired() {
        if (twoAdult) {
            return adults + pensioners < 2;
        }
        return adults + pensioners < 3;
    }

    @Override
    public boolean pensionerAllowed() {
        if (twoAdult) {
            return false;
        }
        return true;
    }
}
