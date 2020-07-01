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
                LOGGER.info("Starting " + getName());
                start();
                tick(t);
                inLockdown = true;
            }
        } else {
            tick(t);

            if (t.equals(end)) {
                LOGGER.info("Ending " + getName());
                end();
                inLockdown = false;
            }
        }
    }

    public Time getEnd() {
        return end;
    }

    protected abstract void start();
    protected abstract void end();

    // tick allows dynamic lockdown behaviour over the course of a lockdown
    protected abstract void tick(Time t);
    
    protected abstract String getName();
    
    public void setPopulation(Population p) {
        population = p;
    }
}
