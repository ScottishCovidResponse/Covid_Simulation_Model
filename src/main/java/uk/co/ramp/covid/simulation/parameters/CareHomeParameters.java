package uk.co.ramp.covid.simulation.parameters;

import java.util.Objects;

public class CareHomeParameters {
    /** Adjustment in transmission rates for known cases to during staff interactions  */
    public Double PPETransmissionReduction = null;

    /** Number of hours after a care home resident becomes symptomatic before they are
     * removed form the general population */
    public Integer hoursAfterSymptomsBeforeQuarantine = null;
    
    public boolean isValid() {
        return PPETransmissionReduction >= 0
                && hoursAfterSymptomsBeforeQuarantine >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CareHomeParameters that = (CareHomeParameters) o;
        return Objects.equals(PPETransmissionReduction, that.PPETransmissionReduction) &&
                Objects.equals(hoursAfterSymptomsBeforeQuarantine, that.hoursAfterSymptomsBeforeQuarantine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PPETransmissionReduction, hoursAfterSymptomsBeforeQuarantine);
    }
}
