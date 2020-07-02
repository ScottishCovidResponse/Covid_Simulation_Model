package uk.co.ramp.covid.simulation.lockdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public abstract class LockdownComponent {
    private static final Logger LOGGER = LogManager.getLogger(LockdownComponent.class);

    protected Time start;
    protected Time end;
    
    private boolean inLockdown = false;

    // The Population we are controlling
    // Transient to avoid deserialising this for now
    protected transient Population population = null;

    public LockdownComponent(Time start, Time end, Population p) {
        this.start = start;
        this.end = end;
        population = p;
    }

    public void handleLockdown(Time t) {
        if (!inLockdown) {
            if (t.equals(start)) {
                LOGGER.info("Applying " + getName());
                start();
                tick(t);
                inLockdown = true;
            }
        } else {
            tick(t);

            // We allow components with no end, i.e. for easing that takes place on a single day
            if (end != null && t.equals(end)) {
                LOGGER.info("Ending " + getName());
                end();
                inLockdown = false;
            }
        }
    }

    public Time getEnd() {
        return end;
    }

    // Hook points
    protected abstract void start();
    protected void end() {}

    // tick allows dynamic lockdown behaviour over the course of a lockdown
    protected void tick(Time t) {}
    
    protected abstract String getName();
    
    public void setPopulation(Population p) {
        population = p;
    }
}
