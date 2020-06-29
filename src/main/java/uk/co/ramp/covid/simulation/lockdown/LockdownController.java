package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Population;

import java.util.ArrayList;
import java.util.List;

public class LockdownController {
    private boolean schoolLockdown = false;
    
    private double socialDist = 1.0;
    private boolean inLockdown = false;
    
    private final Population population;
    
    private List<LockdownComponent> components;

    public LockdownController(Population p) {
        population = p;
        components = new ArrayList<>();
    }
    
    public LockdownController addComponent(LockdownComponent c) {
        components.add(c);
        return this;
    }

    public void implementLockdown(Time now) {
        for (LockdownComponent c : components) {
            c.handleLockdown(now);
        }
    }

    public boolean inLockdown(Time t) {
       return inLockdown;
    }
}
