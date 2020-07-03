package uk.co.ramp.covid.simulation.lockdown;

import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Population;

import java.util.List;

/** Lockdown event generators do not control lockdowns themselves, but can conditionally generate events that do */
public abstract class LockdownEventGenerator {
    protected Time start = null;
    protected Time end = null;

    // Transient to avoid deserialisation
    protected transient Population population = null;
    
    public LockdownEventGenerator() {}
    
    public List<LockdownEvent> generate(Time t) {
        if (t.compareTo(start) >= 0 && (end == null || t.compareTo(end) < 0)) {
            return generateEvents(t);
        }
        return null;
    }
    
    protected abstract List<LockdownEvent> generateEvents(Time now);

    protected boolean isValid() {
        return start != null;
    }
    
    public void setPopulation(Population p) {
        population = p;
    }
    
}
