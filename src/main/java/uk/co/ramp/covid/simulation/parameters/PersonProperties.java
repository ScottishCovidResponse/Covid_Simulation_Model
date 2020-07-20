package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class PersonProperties {
    /** Chance a person will quarantine themselves if they develop symptoms */
    public Probability pQuarantinesIfSymptomatic = null;

    /** (min) Time in hours after symptoms before tsting/quaratining */
    public Integer symptomToQuarantineDelayHours = null;
    public Integer symptomToTestingDelayHours = null;

    /** Under 20s susceptibility constant */
    public Double susceptibleChildConstant = null;
    
    public boolean isValid() {
        return symptomToQuarantineDelayHours >= 0
                && symptomToTestingDelayHours >= 0
                && susceptibleChildConstant >= 0;
    }
}
