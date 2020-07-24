package uk.co.ramp.covid.simulation.lockdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocalLockdownEventGenerator extends LockdownEventGenerator {
    private static final Logger LOGGER = LogManager.getLogger(LocalLockdownEventGenerator.class);

    private Double newCasesThreshold = null;
    private Integer numHospitalisedThreshold = null;
    private Double socialDistance = null;
    
    private int prevInfected = 0;
    private boolean gotInitialInfections = false;

    private int getNumInfected() {
        int inf = 0;
        for (Person p : population.getAllPeople()) {
            if (p.isInfected()) {
                inf++;
            }
        }
        return inf;
    }

    private int getNumHospitalised() {
        int inHospital = 0;
        for (Person p : population.getAllPeople()) {
            if (p.isHospitalised()) {
                inHospital++;
            }
        }
        return inHospital;
    }

    private List<LockdownEvent> handleNewCasesLockdown(Time t) {
        List<LockdownEvent> newEvents = new ArrayList<>();

        int infected = getNumInfected();
        int newCases = infected - prevInfected;
        prevInfected = infected;

        // We need to skip the first day the generator is active so we only respond to *new* cases
        if (gotInitialInfections) {
            if ((double) newCases / population.getAllPeople().size() >= newCasesThreshold) {
                LOGGER.info("New cases threshold breached. Applying lockdown");
                newEvents.add(new FullLockdownEvent(t, population, socialDistance));
            }
        }

        gotInitialInfections = true;
        return newEvents;
    }

    @Override
    protected List<LockdownEvent> generateEvents(Time now) {
        List<LockdownEvent> events = handleNewCasesLockdown(now);

        // No point generating 2 lockdown events so we can finish here
        if (!events.isEmpty()) {
            return events;
        }

        return handleHospitalisedLockdown(now);
    }

    private List<LockdownEvent> handleHospitalisedLockdown(Time now) {
        List<LockdownEvent> newEvents = new ArrayList<>();

        if (getNumHospitalised() >= numHospitalisedThreshold) {
            LOGGER.info("Hospitalisation threshold breached. Applying lockdown");
            newEvents.add(new FullLockdownEvent(now, population, socialDistance));
        }

        return newEvents;
    }

    @Override
    public boolean isValid() {
        return super.isValid()
                && newCasesThreshold != null
                && numHospitalisedThreshold != null
                && socialDistance != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalLockdownEventGenerator that = (LocalLockdownEventGenerator) o;
        return prevInfected == that.prevInfected &&
                gotInitialInfections == that.gotInitialInfections &&
                Objects.equals(newCasesThreshold, that.newCasesThreshold) &&
                Objects.equals(numHospitalisedThreshold, that.numHospitalisedThreshold) &&
                Objects.equals(socialDistance, that.socialDistance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newCasesThreshold, numHospitalisedThreshold, socialDistance, prevInfected, gotInitialInfections);
    }
}
