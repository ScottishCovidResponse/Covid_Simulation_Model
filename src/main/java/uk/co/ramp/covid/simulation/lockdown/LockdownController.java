package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Population;

public class LockdownController {
    
    private Time lockdownStart;
    private Time lockdownEnd;
    
    private final Population population;

    public LockdownController(Population p) {
        population = p;
    }

    public LockdownController(Population p, Time startDay, Time endDay) {
        this(p);
        lockdownStart = startDay;
        lockdownEnd = endDay;
    }

    public void implementLockdown(Time now) {
        if (now == lockdownStart) {
            enterLockdown();
        } else if (now == lockdownEnd) {
            exitLockdown();
        }
    }

    private void exitLockdown() { }

    private void enterLockdown() {
        for (Person p : population.getAllPeople()) {
            p.furlough();
        }
    }

}
