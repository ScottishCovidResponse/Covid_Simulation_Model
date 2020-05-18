package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.DailyStats;

public class Pensioner extends Person {

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsPensioner();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsPensioner();
    }

}
