package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.util.RNG;

/** LargeFamily: two adults of any age and three or more children,
 *  or three or more adults of any age and one or more children */
public class LargeFamily extends Household {

    private boolean twoAdult = RNG.get().nextUniform(0,1) < 0.5;

    public LargeFamily(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        if (twoAdult) {
            return adults + pensioners < 2;
        }
        return adults + pensioners < 3;
    }

    @Override
    public boolean additionalAdultsAllowed() {
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
    public boolean additionalChildrenAllowed() {
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
    public boolean additionalPensionersAllowed() {
        if (twoAdult) {
            return false;
        }
        return true;
    }
}
