package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

public class CovidTestParameters {
    public Probability diagnosticTestSensitivity = null;
    public Probability pDiagnosticTestAvailable = null;

    public boolean isValid() {
        return true;
    }
}