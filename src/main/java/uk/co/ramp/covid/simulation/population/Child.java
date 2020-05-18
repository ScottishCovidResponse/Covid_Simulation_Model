package uk.co.ramp.covid.simulation.population;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.place.CommunalPlace;
import uk.co.ramp.covid.simulation.place.School;

public class Child extends Person {

    public Child() { }

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
    public void allocateCommunalPlace(Population p) {
        CommunalPlace property = p.getRandomPlace();
        while (!(property instanceof School)) property = p.getRandomPlace();
        this.setMIndex(property.getIndex());
    }
}
