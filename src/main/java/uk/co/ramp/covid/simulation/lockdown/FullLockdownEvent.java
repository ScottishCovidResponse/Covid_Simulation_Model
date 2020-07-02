package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
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
