package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.OpeningTimes;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ShiftAllocator;

public class BuildingTimeParameters {
    public OpeningTimes openingTime = null;
    public ShiftAllocator shifts = null;

    // Sizes can be chosen probabilistically/based on the size condition. Both of these don't need to be set.
    public CommunalPlace.Size sizeCondition = null;
    public Probability probability = null;

    // TODO: Implement
    public boolean isValid() {
        return true;
    }
}
