package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class ConstructionSite extends CommunalPlace {

    public ConstructionSite(int cindex) {
        super(cindex);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpConstructionSiteTrans();
        keyProb = PopulationParameters.get().getpConstructionSiteKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;

    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionConstructionSite();
    }



}
