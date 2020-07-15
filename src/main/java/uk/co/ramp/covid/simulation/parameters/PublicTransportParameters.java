package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class PublicTransportParameters {
    /** Expected interactions on public transport */
    public Double expectedInteractions = null;

    /** Probability a households requires public transport */
    public Probability pFamilyTakesTransport = null;

    public boolean isValid() {
        return expectedInteractions >= 0;
    }
}
