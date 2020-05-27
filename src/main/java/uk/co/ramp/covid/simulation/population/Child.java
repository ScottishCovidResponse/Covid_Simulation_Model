package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;

public class Child extends Person {

    public Child() {
        shifts = Shifts.schoolTimes();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsChild();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsChild();
    }

    // All children go to school
    @Override
    public void allocateCommunalPlace(Places p) {
        setPrimaryPlace(p.getRandomSchool());
    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().getChildProgressionPhase2();
    }
}
