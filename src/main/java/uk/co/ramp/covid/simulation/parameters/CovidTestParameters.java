package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class CovidTestParameters {
    public Probability pDiagnosticTestDetectsSuccessfully = null;

    /** Probability is it possible to be tested in a given hour */
    public Probability pDiagnosticTestAvailableHour = null;
}
