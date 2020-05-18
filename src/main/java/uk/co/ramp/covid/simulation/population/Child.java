package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.DailyStats;

public class Child extends Person {

    public Child() {
        super.setSchool(true);
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsChild();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsChild();
    }
}
