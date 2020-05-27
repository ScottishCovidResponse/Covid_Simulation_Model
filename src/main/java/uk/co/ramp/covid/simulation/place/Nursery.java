package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class Nursery extends CommunalPlace {

    public Nursery() {
        this(Size.UNKNOWN);
    }

    public Nursery(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpNurseryTrans();
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsNursery();
    }

    @Override
    public Shifts getShifts() {
        return Shifts.schoolTimes();
    }
}
