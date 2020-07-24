package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

public class PensionerProperties {
    /** Anyone >= minAgeToEnterCare may be assigned a CareHome with probability pEntersCareHome */
    public Integer minAgeToEnterCare = null;
    public Probability pEntersCareHome = null;
    
    public boolean isValid() {
        return minAgeToEnterCare >= 65 && minAgeToEnterCare <= 100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PensionerProperties that = (PensionerProperties) o;
        return Objects.equals(minAgeToEnterCare, that.minAgeToEnterCare) &&
                Objects.equals(pEntersCareHome, that.pEntersCareHome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minAgeToEnterCare, pEntersCareHome);
    }
}
