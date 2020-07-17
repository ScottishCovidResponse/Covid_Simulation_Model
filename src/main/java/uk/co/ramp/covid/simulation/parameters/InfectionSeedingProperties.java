package uk.co.ramp.covid.simulation.parameters;

public class InfectionSeedingProperties {
    /** Initial seed rates (per day) based on person type */
    public Double initialSeedAdult = null;
    public Double initialSeedInfantChildPensioner = null;

    /** Daily increase in rates (power-law) */
    public Double rateIncreaseSeed = null;
}
