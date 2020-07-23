package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

import java.util.Objects;

public class ShieldingEasingEvent extends LockdownEvent {

    // Can either partially lift shielding, allowing neighbour visits, or lift it fully
    private boolean partial = false;
    private Probability partialShieldProbability = null;

    public ShieldingEasingEvent(Time start, Population p ) {
        super(start, p);
    }

    public ShieldingEasingEvent(Time start, Population p, Probability partialShieldProbability) {
        super(start, p);
        this.partial = true;
        this.partialShieldProbability = partialShieldProbability;
    }

    @Override
    protected void apply() {
        for (Household h : population.getHouseholds()) {
            if (partial) {
                h.startPartialShielding(partialShieldProbability);
            } else {
                h.stopShielding();
            }
        }
    }

    @Override
    protected String getName() {
        return "ShieldingEasing";
    }

    @Override
    protected boolean isValid() {
        return !partial || partialShieldProbability != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShieldingEasingEvent that = (ShieldingEasingEvent) o;
        return partial == that.partial &&
                Objects.equals(partialShieldProbability, that.partialShieldProbability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), partial, partialShieldProbability);
    }
}
