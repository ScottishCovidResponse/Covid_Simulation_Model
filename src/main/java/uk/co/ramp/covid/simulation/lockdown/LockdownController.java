package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.util.Time;
import java.util.ArrayList;
import java.util.List;

public class LockdownController {
    private final List<LockdownEvent> events;
    private final List<LockdownEventGenerator> eventGenerators;

    public LockdownController() {
        events = new ArrayList<>();
        eventGenerators = new ArrayList<>();
    }
    
    public void addComponent(LockdownEvent c) {
        if (!c.isValid()) {
            throw new InvalidLockdownEventException(c.getName());
        }
        events.add(c);
    }

    public void addComponent(LockdownEventGenerator c) {
        if (!c.isValid()) {
            throw new InvalidLockdownEventException("Invalid event generator");
        }
        eventGenerators.add(c);
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
