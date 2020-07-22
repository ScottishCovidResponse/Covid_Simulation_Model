package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.List;
import java.util.Objects;

public class RestaurantEasingEvent extends CommunalPlaceEasingEvent {

    private final Double visitFrequencyAdjustment;

    public RestaurantEasingEvent(Time s, Population p, Probability keyPremises, Double socialDistance,
                           Double visitFrequencyAdjustment) {
        super(s, p, keyPremises, socialDistance);
        this.visitFrequencyAdjustment = visitFrequencyAdjustment;
    }

    @Override
    protected void apply() {
        super.apply();
        for (Household h : population.getHouseholds()) {
            h.setLockdownRestaurantVisitFrequencyAdjustment(visitFrequencyAdjustment);
        }
    }

    @Override
    protected List<? extends CommunalPlace> getPlaces() {
        return population.getPlaces().getRestaurants();
    }

    @Override
    protected String getName() {
        return "RestaurantEasing";
    }

    protected boolean isValid() {
        return super.isValid() && visitFrequencyAdjustment != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RestaurantEasingEvent that = (RestaurantEasingEvent) o;
        return Objects.equals(visitFrequencyAdjustment, that.visitFrequencyAdjustment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), visitFrequencyAdjustment);
    }
}
