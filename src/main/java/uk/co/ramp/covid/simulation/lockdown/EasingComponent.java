package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

/** Lockdown component to close key places */
public class EasingComponent extends LockdownComponent {
    
    private String placeType = null;

    public EasingComponent(Time s, Time e, Population p) {
        super(s, e, p);
    }

    @Override
    protected void start() {

    }

    @Override
    protected String getName() {
        return "Closure Component";
    }
}
