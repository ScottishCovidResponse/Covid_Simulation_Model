package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class Nursery extends CommunalPlace {

    public Nursery() {
        this(Size.UNKNOWN);
    }

    public Nursery(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpNurseryTrans();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsNursery();
    }

    @Override
    public void doMovement(int day, int hour) {
        moveShifts(day, hour);
    }
}
