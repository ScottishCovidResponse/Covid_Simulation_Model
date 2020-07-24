package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.util.Probability;

import java.util.Objects;

public class InfantProperties {
    /** Probability an infant attends nursery - fixed during the run */
    public Probability pAttendsNursery = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfantProperties that = (InfantProperties) o;
        return Objects.equals(pAttendsNursery, that.pAttendsNursery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pAttendsNursery);
    }
}
