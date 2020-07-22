package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;
import java.util.Objects;

/** Lockdown component to ease restrictions on places */
public abstract class CommunalPlaceEasingEvent extends LockdownEvent {
    private final Probability keyPremises;
    private final Double socialDistance;


    public CommunalPlaceEasingEvent(Time s, Population p, Probability keyPremises, Double socialDistance) {
        super(s, p);
        this.keyPremises = keyPremises;
        this.socialDistance = socialDistance;
    }

    @Override
    protected void apply() {
        List<? extends CommunalPlace> places = getPlaces();
        for (CommunalPlace p : places) {
            p.overrideKeyPremises(keyPremises.sample());
            p.setSocialDistancing(socialDistance);
        }
    }

    protected abstract List<? extends CommunalPlace> getPlaces();

    @Override
    protected boolean isValid() {
        return start != null
                && keyPremises != null
                && socialDistance != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommunalPlaceEasingEvent that = (CommunalPlaceEasingEvent) o;
        return Objects.equals(keyPremises, that.keyPremises) &&
                Objects.equals(socialDistance, that.socialDistance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), keyPremises, socialDistance);
    }
}
