package uk.co.ramp.covid.simulation.parameters;

import java.util.Objects;

public class InfectionSeedingProperties {
    /** Initial seed rates (per day) based on person type */
    public Double initialSeedAdult = null;
    public Double initialSeedInfantChildPensioner = null;

    /** Daily increase in rates (power-law) */
    public Double rateIncreaseSeed = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfectionSeedingProperties that = (InfectionSeedingProperties) o;
        return Objects.equals(initialSeedAdult, that.initialSeedAdult) &&
                Objects.equals(initialSeedInfantChildPensioner, that.initialSeedInfantChildPensioner) &&
                Objects.equals(rateIncreaseSeed, that.rateIncreaseSeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initialSeedAdult, initialSeedInfantChildPensioner, rateIncreaseSeed);
    }
}
