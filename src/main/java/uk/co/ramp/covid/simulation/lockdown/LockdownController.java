package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Population;

import java.util.ArrayList;
import java.util.List;

public class LockdownController {
    private final Population population;
    private List<LockdownEvent> events;
    private List<LockdownEventGenerator> eventGenerators;

    public LockdownController(Population p) {
        population = p;
        events = new ArrayList<>();
        eventGenerators = new ArrayList<>();
    }
    
    public LockdownController addComponent(LockdownEvent c) {
        if (!c.isValid()) {
            throw new InvalidLockdownEventException(c.getName());
        }
        events.add(c);
        return this;
    }

    public LockdownController addComponent(LockdownEventGenerator c) {
        if (!c.isValid()) {
            throw new InvalidLockdownEventException("Invalid event generator");
        }
        eventGenerators.add(c);
        return this;
    }

    public void implementLockdown(Time now) {
        // Generate any new events first
        for (LockdownEventGenerator gen : eventGenerators) {
            List<LockdownEvent> es = gen.generate(now);
            if (es != null) {
                events.addAll(es);
            }
        }

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
}
