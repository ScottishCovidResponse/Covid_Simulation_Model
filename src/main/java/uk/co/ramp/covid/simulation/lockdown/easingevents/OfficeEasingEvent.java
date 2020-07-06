package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.place.Office;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

public class OfficeEasingEvent extends CommunalPlaceEasingEvent {

    public OfficeEasingEvent(Time s, Population p, Probability keyPremises, Double socialDistance) {
        super(s, p, keyPremises, socialDistance);
    }

    @Override
    protected List<Office> getPlaces() {
        return population.getPlaces().getOffices();
    }

    @Override
    protected String getName() {
        return "OfficeEasing";
    }
}
