package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

public class NurseryEasingEvent extends CommunalPlaceEasingEvent {
    // TODO: Should nurseries furlough students like schools?

    public NurseryEasingEvent(Time s, Population p, Probability keyPremises, Double socialDistance) {
        super(s, p, keyPremises, socialDistance);
    }

    @Override
    protected List<? extends CommunalPlace> getPlaces() {
        return population.getPlaces().getNurseries();
    }

    @Override
    protected String getName() {
        return "NurseryEasing";
    }
}
