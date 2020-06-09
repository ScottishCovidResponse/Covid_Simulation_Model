package uk.co.ramp.covid.simulation.parameters;

import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;

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


}
