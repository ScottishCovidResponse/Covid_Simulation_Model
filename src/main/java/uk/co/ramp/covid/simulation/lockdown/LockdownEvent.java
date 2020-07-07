package uk.co.ramp.covid.simulation.lockdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public abstract class LockdownEvent {
    private static final Logger LOGGER = LogManager.getLogger(LockdownEvent.class);

    protected Time start;

    // The Population we are controlling
    // Transient to avoid de-serialising this for now
    protected transient Population population;

    public LockdownEvent(Time start, Population p) {
        this.start = start;
        population = p;
    }

    public void handleLockdown(Time t) {
        if (t.equals(start)) {
            LOGGER.info("Applying " + getName());
            apply();
        }
    }

    // Hook points
    protected abstract void apply();
    protected abstract String getName();
    protected abstract boolean isValid();
    
    public void setPopulation(Population p) {
        population = p;
    }
}
