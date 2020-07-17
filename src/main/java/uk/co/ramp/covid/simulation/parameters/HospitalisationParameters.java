package uk.co.ramp.covid.simulation.parameters;

public class HospitalisationParameters {
    /** Reduction to account for PPE when staff are visiting a known COVID case */
    public Double hospitalisationTransmissionReduction = null;
    
    public boolean isValid() {
        return hospitalisationTransmissionReduction >= 0;
    }
}
