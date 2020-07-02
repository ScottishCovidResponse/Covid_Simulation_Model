package uk.co.ramp.covid.simulation.lockdown;

public class InvalidLockdownEventException extends RuntimeException {
    public InvalidLockdownEventException(String name) {
        super(name);
    }
}
