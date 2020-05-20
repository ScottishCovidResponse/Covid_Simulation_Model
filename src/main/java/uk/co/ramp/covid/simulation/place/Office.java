package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Office extends CommunalPlace {
    public Office() {
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpOfficeTrans();
        keyProb = PopulationParameters.get().getpOfficeKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionOffice();
    }

}
