package uk.co.ramp.covid.simulation.util;

public class Probability {
    double p;

    public Probability(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new InvalidProbabilityException("Trying to initialise a probability with p = " + p);
        }
        this.p = p;
    }
    
    public double asDouble() { return p; }
    
    public void set(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new InvalidProbabilityException("Trying to initialise a probability with p = " + p);
        }
        this.p = p;
    }
}
