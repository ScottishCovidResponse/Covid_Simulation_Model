package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

public class PublicTransportParameters {
    /** Expected interactions on public transport */
    public Double expectedInteractions = null;

    /** Probability a households requires public transport */
    public Probability pFamilyTakesTransport = null;

    public boolean isValid() {
        return expectedInteractions >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicTransportParameters that = (PublicTransportParameters) o;
        return Objects.equals(expectedInteractions, that.expectedInteractions) &&
                Objects.equals(pFamilyTakesTransport, that.pFamilyTakesTransport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expectedInteractions, pFamilyTakesTransport);
    }
}
