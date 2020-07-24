package uk.co.ramp.covid.simulation.parameters;

import java.util.Objects;

public class HospitalisationParameters {
    /** Reduction to account for PPE when staff are visiting a known COVID case */
    public Double hospitalisationTransmissionReduction = null;
    
    public boolean isValid() {
        return hospitalisationTransmissionReduction >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HospitalisationParameters that = (HospitalisationParameters) o;
        return Objects.equals(hospitalisationTransmissionReduction, that.hospitalisationTransmissionReduction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospitalisationTransmissionReduction);
    }
}
