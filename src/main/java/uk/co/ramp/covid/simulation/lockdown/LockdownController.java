package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Population;

import java.util.ArrayList;
import java.util.List;

public class LockdownController {
    private final Population population;
    private List<LockdownEvent> events;

    public LockdownController(Population p) {
        population = p;
        events = new ArrayList<>();
    }
    
    public LockdownController addComponent(LockdownEvent c) {
        if (!c.isValid()) {
            throw new InvalidLockdownEventException(c.getName());
        }
        events.add(c);
        return this;
    }

    public void implementLockdown(Time now) {
        List<LockdownEvent> finished = new ArrayList<>();
        for (LockdownEvent c : events) {
            c.handleLockdown(now);

            // Remove events that should never fire again
            if (c.start.equals(now)) {
                finished.add(c);
            }
        }
        events.removeAll(finished);
    }

    public boolean inLockdown(Time t) {
        // TODO: this will be propogated insde the classes to we don't pass this around.
       return false;
    }
}
