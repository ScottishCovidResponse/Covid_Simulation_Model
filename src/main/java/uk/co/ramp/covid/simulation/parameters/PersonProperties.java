package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class PersonProperties {
    public Probability pQuarantine = null;
    public Probability pTransmission = null;

    public boolean isValid() {
        return true;
    }
}
