package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

/** Lockdown component to ease restrictions on places */
public abstract class CommunalPlaceEasingEvent extends LockdownEvent {
    private Probability keyPremises = null;
    private Double socialDistance = null;


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
}
