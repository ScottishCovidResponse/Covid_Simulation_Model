package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public class FullLockdownEvent extends LockdownEvent {
    
    private Double socialDistance = null;

    public FullLockdownEvent(Time s, Population p, double socialDistance) {
        super(s, p);
        this.socialDistance = socialDistance;
    }

    @Override
    protected void apply() {
        for (CommunalPlace cPlace : population.getPlaces().getAllPlaces()) {
            cPlace.enterLockdown(socialDistance);
        }
        
        for (Person p : population.getAllPeople()) {
            p.furlough();
        }

        for (Household h : population.getHouseholds()) {
            // TODO: This should be settable
            h.setLockdownShopVisitFrequencyAdjustment(0.5);
            h.setLockdownRestaurantVisitFrequencyAdjustment(0.0);
            // Note: Only applies to those that are lockdown compliant
            h.setLockdownNeighbourVisitFrequencyAdjustment(0.0);
        }
    }


    @Override
    protected String getName() {
        return "Full Lockdown";
    }

    @Override
    protected boolean isValid() {
        return start != null && socialDistance != null;
    }
}
