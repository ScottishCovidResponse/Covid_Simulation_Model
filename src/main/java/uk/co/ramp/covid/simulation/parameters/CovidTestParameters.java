package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

public class CovidTestParameters {
    public Probability pDiagnosticTestDetectsSuccessfully = null;

    /** Probability is it possible to be tested in a given hour */
    public Probability pDiagnosticTestAvailableHour = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CovidTestParameters that = (CovidTestParameters) o;
        return Objects.equals(pDiagnosticTestDetectsSuccessfully, that.pDiagnosticTestDetectsSuccessfully) &&
                Objects.equals(pDiagnosticTestAvailableHour, that.pDiagnosticTestAvailableHour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pDiagnosticTestDetectsSuccessfully, pDiagnosticTestAvailableHour);
    }
}
