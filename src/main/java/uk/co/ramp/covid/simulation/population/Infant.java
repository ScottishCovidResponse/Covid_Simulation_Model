package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.Nursery;

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

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsInfant();
    }

    @Override
    public void reportDeath(DailyStats s) {
        s.incDeathsInfant();
    }

    @Override
    public void allocateCommunalPlace(Population p) {
        CommunalPlace property = p.getRandomPlace();
        while (!(property instanceof Nursery)) property = p.getRandomPlace();
        this.setMIndex(property.getIndex());
    }
}
