package uk.co.ramp.covid.simulation.lockdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

public abstract class LockdownComponent {
    private static final Logger LOGGER = LogManager.getLogger(LockdownComponent.class);

    protected Time startTime;
    protected Time endTime;
    
    private boolean inLockdown = false;
    
    // The Population we are controlling
    protected Population population;
    
    public LockdownComponent(Time s, Time e, Population p) {
        startTime = s;
        endTime = e;
        population = p;
    }

    public void handleLockdown(Time t) {
        if (!inLockdown) {
            if (t.equals(startTime)) {
                LOGGER.info("Starting " + getName());
                start();
                tick(t);
                inLockdown = true;
            }
        } else {
            tick(t);

            if (t.equals(endTime)) {
                LOGGER.info("Ending " + getName());
                end();
                inLockdown = false;
            }
        }
    }

    public Time getEndTime() {
        return endTime;
    }

    protected abstract void start();
    protected abstract void end();

    // tick allows dynamic lockdown behaviour over the course of a lockdown
    protected abstract void tick(Time t);
    
    protected abstract String getName();
}
