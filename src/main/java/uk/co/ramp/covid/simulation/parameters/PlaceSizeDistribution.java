package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

import java.util.Objects;

public class PlaceSizeDistribution {
    public Probability pSmall = null;
    public Probability pMed = null;
    public Probability pLarge = null;
    
    public ProbabilityDistribution<CommunalPlace.Size> sizeDistribution() {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution<>();
        p.add(pSmall, CommunalPlace.Size.SMALL);
        p.add(pMed, CommunalPlace.Size.MED);
        p.add(pLarge, CommunalPlace.Size.LARGE);
        return p;
    }

    public boolean isValid() {
        return sizeDistribution().isValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceSizeDistribution that = (PlaceSizeDistribution) o;
        return Objects.equals(pSmall, that.pSmall) &&
                Objects.equals(pMed, that.pMed) &&
                Objects.equals(pLarge, that.pLarge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pSmall, pMed, pLarge);
    }
}
