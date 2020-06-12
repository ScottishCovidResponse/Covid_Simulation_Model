package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;

/** LargeManyAdultFamily: 3 or more (any age) adults + 1 or more children */
public class LargeManyAdultFamily extends Household {

    public LargeManyAdultFamily() {
        super();
    }

    @Override
    public boolean adultRequired() {
        return adults + pensioners < 3;
    }

    @Override
    public boolean additionalAdultsAllowed() {
        return true;
    }

    @Override
    public boolean childRequired() {
        return children < 1;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return true;
    }

    @Override
    public boolean pensionerRequired() {
        return adultRequired();
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return additionalAdultsAllowed();
    }

    @Override
    public boolean adultAnyAgeRequired() {
        return adults + pensioners < 3;
    }

    @Override
    public boolean additionalAdultAnyAgeAllowed() {
        return true;
    }
}
