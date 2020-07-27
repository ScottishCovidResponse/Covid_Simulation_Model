package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.OpeningTimes;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ShiftAllocator;

public class BuildingTimeParameters {
    public final OpeningTimes openingTime = null;
    public final ShiftAllocator shifts = null;

    // Sizes can be chosen probabilistically/based on the size condition. Both of these don't need to be set.
    public final CommunalPlace.Size sizeCondition = null;
    public final Probability probability = null;

    public boolean isValid() {
        return openingTime != null
                && shifts != null
                && (probability != null || sizeCondition != null);
    }
}
