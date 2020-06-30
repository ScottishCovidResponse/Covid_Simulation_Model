package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public class FullLockdownComponent extends LockdownComponent {
    
    private double sDist;

    public FullLockdownComponent(Time s, Time e, Population p, double sDist) {
        super(s, e, p);
        this.sDist = sDist;
    }

    @Override
    protected void start() {
        for (CommunalPlace cPlace : population.getPlaces().getAllPlaces()) {
            cPlace.enterLockdown(sDist);
        }
        
        for (Person p : population.getAllPeople()) {
            p.furlough();
        }
    }

    @Override
    protected void end() {
        for (CommunalPlace cPlace : population.getPlaces().getAllPlaces()) {
            cPlace.exitLockdown();
        }

        for (Person p : population.getAllPeople()) {
            p.unFurlough();
        }
    }

    @Override
    protected void tick(Time t) {

    }

    @Override
    protected String getName() {
        return "Full Lockdown";
    }
}
