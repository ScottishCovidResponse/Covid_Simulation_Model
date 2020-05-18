package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.CovidParameters;
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

    @Override
    public void allocateCommunalPlace(Population p) {}

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().getPensionerProgressionPhase2();
    }
}
