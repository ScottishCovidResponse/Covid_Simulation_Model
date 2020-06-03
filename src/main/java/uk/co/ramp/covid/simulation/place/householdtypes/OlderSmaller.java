package uk.co.ramp.covid.simulation.place.householdtypes;

import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.util.RNG;

/** OlderSmaller: one adult aged 16-64 and one of pensionable age and no children,
 *  or two adults of pensionable age and no children */
public class OlderSmaller extends Household {
    private boolean doublePen = RNG.get().nextUniform(0,1) < 0.8;

    public OlderSmaller(Places places) {
        super(places);
    }

    @Override
    public boolean adultRequired() {
        if (doublePen) {
            return false;
        }
        return adults < 1;
    }

    @Override
    public boolean additionalAdultsAllowed() {
        return false;
    }

    @Override
    public boolean childRequired() {
        return false;
    }

    @Override
    public boolean additionalChildrenAllowed() {
        return false;
    }

    @Override
    public boolean pensionerRequired() {
        if (doublePen) {
            return pensioners < 2;
        }
        return pensioners < 1;
    }

    @Override
    public boolean additionalPensionersAllowed() {
        return false;
    }
}
