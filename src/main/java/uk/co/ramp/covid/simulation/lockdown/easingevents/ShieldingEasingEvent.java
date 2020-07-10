package uk.co.ramp.covid.simulation.lockdown.easingevents;

import uk.co.ramp.covid.simulation.lockdown.LockdownEvent;
import uk.co.ramp.covid.simulation.place.Household;
import uk.co.ramp.covid.simulation.population.Population;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.Time;

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
}
