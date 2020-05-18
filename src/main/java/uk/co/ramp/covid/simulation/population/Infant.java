package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.DailyStats;

public class Infant extends Person {
    public Infant() {
        this.setNursery();
    }

    private void setNursery() {
        if (rng.nextUniform(0, 1) < PopulationParameters.get().getpAttendsNursery()) {
            super.setNursery(true);
        }
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsInfant();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsInfant();
    }
}
