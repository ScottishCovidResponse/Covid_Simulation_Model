package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

public class TravelEasingEvent extends LockdownEvent {
    private final Probability pTravelSeed;

    public TravelEasingEvent(Time start, Population p, Probability pTravelSeed) {
        super(start, p);
        this.pTravelSeed = pTravelSeed;
    }

    @Override
    protected void apply() {
        population.getSeeder().startTravelSeeding(pTravelSeed);
    }

    @Override
    protected String getName() {
        return "Travel Easing";
    }

    @Override
    protected boolean isValid() {
        return pTravelSeed != null;
    }
}
