package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.CovidParameters;
import uk.co.ramp.covid.simulation.DailyStats;

public class Infant extends Person {

    private boolean goesToNursery;

    public Infant() {
        setNursery();
    }

    private void setNursery() {
        if (rng.nextUniform(0, 1) < PopulationParameters.get().getpAttendsNursery()) {
            goesToNursery = true;
        } else {
            goesToNursery = false;
        }
    }

    public boolean isGoesToNursery() {
        return goesToNursery;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsInfant();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsInfant();
    }

    @Override
    public void allocateCommunalPlace(Places p) {
        if (goesToNursery) {
            setPrimaryPlace(p.getRandomNursery());
        }

    }

    @Override
    public boolean avoidsPhase2(double testP) {
        return testP > CovidParameters.get().getChildProgressionPhase2();
    }
}
