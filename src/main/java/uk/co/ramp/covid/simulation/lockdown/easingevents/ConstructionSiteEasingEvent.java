package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.place.ConstructionSite;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

public class ConstructionSiteEasingEvent extends CommunalPlaceEasingEvent {

    public ConstructionSiteEasingEvent(Time s, Population p,
                                       Probability keyPremises, Double socialDistance) {
        super(s, p, keyPremises, socialDistance);
    }

    @Override
    protected List<ConstructionSite> getPlaces() {
        return population.getPlaces().getConstructionSites();
    }

    @Override
    protected String getName() {
        return "ConstructionSiteEasing";
    }
}
