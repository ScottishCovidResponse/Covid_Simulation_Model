package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class PersonProperties {
    public Probability pQuarantinesIfSymptomatic = null;
    public Integer symptomToQuarantineDelay = null;
    public Integer symptomToTestingDelay = null;
    public Double pSusceptibleChild = null;
}
