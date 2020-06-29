package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Place;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

/** Lockdown component to close key places */
public class ClosureComponent extends LockdownComponent {

    public ClosureComponent(Time s, Time e, Population p) {
        super(s, e, p);
    }

    @Override
    protected void start() {
        for (CommunalPlace p : population.getPlaces().getAllPlaces()) {
            if (!p.isKeyPremises()) {
                p.close();
            }
        }
    }

    @Override
    protected void end() {
        for (CommunalPlace p : population.getPlaces().getAllPlaces()) {
            p.open();
        }
    }

    @Override
    protected void tick(Time t) {

    }
}
