package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Nursery extends CommunalPlace {
    public Nursery() {
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpNurseryTrans();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsNursery();
    }
}
