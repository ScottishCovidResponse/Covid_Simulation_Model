package uk.co.ramp.covid.simulation.util;

public class CannotGeneratePopulationException extends RuntimeException {
    CannotGeneratePopulationException() {
        super("Could not generate a valid population");
    }
}
