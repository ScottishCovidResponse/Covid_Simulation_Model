package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.Nursery;
import uk.co.ramp.covid.simulation.place.School;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

/** Allows the schols to return at a given time */
public class SchoolReturnComponent extends LockdownComponent {

    public SchoolReturnComponent(Time s, Time e, Population p) {
        super(s, e, p);
    }

    @Override
    protected void start() {
        for (School s : population.getPlaces().getSchools()) {
            s.overrideKeyPremises(true);
        }
        for (Nursery n : population.getPlaces().getNurseries()) {
            n.overrideKeyPremises(true);
        }
    }

    @Override
    protected void end() { }

    @Override
    protected void tick(Time t) { }
}
