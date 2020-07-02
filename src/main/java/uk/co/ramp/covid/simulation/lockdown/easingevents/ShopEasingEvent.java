package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;

public class ShopEasingEvent extends CommunalPlaceEasingEvent {
    
    private Double visitFrequencyAdjustment = null;

    public ShopEasingEvent(Time s, Population p, Probability keyPremises, Double socialDistance,
                           Double visitFrequencyAdjustment) {
        super(s, p, keyPremises, socialDistance);
        this.visitFrequencyAdjustment = visitFrequencyAdjustment;
    }

    @Override
    protected List<? extends CommunalPlace> getPlaces() {
        return population.getPlaces().getShops();
    }

    @Override
    protected String getName() {
        return "ShopEasing";
    }
    
    protected boolean isValid() {
        return super.isValid() && visitFrequencyAdjustment != null;
    }
}
