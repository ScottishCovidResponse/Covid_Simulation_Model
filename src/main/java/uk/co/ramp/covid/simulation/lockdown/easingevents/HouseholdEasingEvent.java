package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.Objects;

public class HouseholdEasingEvent extends LockdownEvent {
    
    private final Double neighbourVisitAdjustment;

    public HouseholdEasingEvent(Time start, Population p, Double neighbourVisitAdjustment) {
        super(start, p);
        this.neighbourVisitAdjustment = neighbourVisitAdjustment;
    }

    @Override
    protected void apply() {
        for (Household h : population.getHouseholds()) {
            h.setLockdownNeighbourVisitFrequencyAdjustment(neighbourVisitAdjustment);
        }
    }

    @Override
    protected String getName() {
        return "HouseholdEasing";
    }

    @Override
    protected boolean isValid() {
        return neighbourVisitAdjustment != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HouseholdEasingEvent that = (HouseholdEasingEvent) o;
        return Objects.equals(neighbourVisitAdjustment, that.neighbourVisitAdjustment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), neighbourVisitAdjustment);
    }
}
