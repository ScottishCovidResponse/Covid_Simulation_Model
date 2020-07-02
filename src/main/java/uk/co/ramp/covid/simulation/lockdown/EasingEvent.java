package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

/** Lockdown component to ease restrictions on places */
public class EasingEvent extends LockdownEvent {
    private String placeType = null;
    private Probability keyPremises = null;
    private Double socialDistance = null;

    public EasingEvent(Time s, Time e, Population p,
                       String placeType, Probability keyPremises, Double socialDistance) {
        super(s, p);
        this.placeType = placeType;
        this.keyPremises = keyPremises;
        this.socialDistance = socialDistance;
    }

    @Override
    protected void apply() {
        List<? extends CommunalPlace> places = population.getPlaces().getByName(placeType);
        for (CommunalPlace p : places) {
            p.overrideKeyPremises(keyPremises.sample());
            p.setSocialDistancing(socialDistance);
        }
    }

    @Override
    protected String getName() {
        return placeType + " easing";
    }

    @Override
    protected boolean isValid() {
        return start != null 
                && keyPremises != null
                && socialDistance != null
                && population.getPlaces().getByName(placeType) != null;
    }
}
