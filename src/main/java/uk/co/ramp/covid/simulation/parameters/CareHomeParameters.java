package uk.co.ramp.covid.simulation.parameters;

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
}
