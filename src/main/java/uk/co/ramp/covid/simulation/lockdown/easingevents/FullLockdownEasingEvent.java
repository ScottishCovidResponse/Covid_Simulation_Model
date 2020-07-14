package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public class FullLockdownEasingEvent extends LockdownEvent {

    public FullLockdownEasingEvent(Time start, Population p) {
        super(start, p);
    }

    @Override
    protected void apply() {
        for (CommunalPlace cPlace : population.getPlaces().getCommunalPlaces()) {
            cPlace.exitLockdown();
        }

        for (Person p : population.getAllPeople()) {
            p.unFurlough();
            p.setLockdownHospitalApptAdjustment(0.0);
        }

        for (Household h : population.getHouseholds()) {
            h.setLockdownShopVisitFrequencyAdjustment(1.0);
            h.setLockdownRestaurantVisitFrequencyAdjustment(1.0);
            h.setLockdownNeighbourVisitFrequencyAdjustment(1.0);
            h.stopShielding();
        }
    }

    @Override
    protected String getName() {
        return "FullLockdownEasing";
    }

    @Override
    protected boolean isValid() {
        return false;
    }
}
