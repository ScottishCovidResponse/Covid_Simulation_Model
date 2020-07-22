package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonProperties that = (PersonProperties) o;
        return Objects.equals(pQuarantinesIfSymptomatic, that.pQuarantinesIfSymptomatic) &&
                Objects.equals(symptomToQuarantineDelayHours, that.symptomToQuarantineDelayHours) &&
                Objects.equals(symptomToTestingDelayHours, that.symptomToTestingDelayHours) &&
                Objects.equals(susceptibleChildConstant, that.susceptibleChildConstant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pQuarantinesIfSymptomatic, symptomToQuarantineDelayHours, symptomToTestingDelayHours, susceptibleChildConstant);
    }
}
