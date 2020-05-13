package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(int cindex) {
        super(cindex);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpConstructionSiteTrans();
        keyProb = PopulationParameters.get().getpConstructionSiteKey();
        if (Math.random() > keyProb) keyPremises = true;

    }
}
