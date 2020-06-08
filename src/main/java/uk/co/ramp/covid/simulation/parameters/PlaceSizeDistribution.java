package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

public class PlaceSizeDistribution {
    public Double pSmall = null;
    public Double pMed = null;
    public Double pLarge = null;
    
    public ProbabilityDistribution<CommunalPlace.Size> sizeDistribution() {
        ProbabilityDistribution<CommunalPlace.Size> p = new ProbabilityDistribution<>();
        p.add(pSmall, CommunalPlace.Size.SMALL);
        p.add(pMed, CommunalPlace.Size.MED);
        p.add(pLarge, CommunalPlace.Size.LARGE);
        return p;
    }

    public boolean isValid(String name) {
        return sizeDistribution().isValid();
    }


}
