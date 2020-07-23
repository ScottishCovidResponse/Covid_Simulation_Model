package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TravelEasingEvent that = (TravelEasingEvent) o;
        return Objects.equals(pTravelSeed, that.pTravelSeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pTravelSeed);
    }
}
