package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
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
    public void reportInfection(int day, int hour, Person p, DailyStats s) {
        if (p.isWorking(this, day, hour)) {
            s.incInfectionsNurseryWorker();
        } else {
            s.incInfectionsNurseryVisitor();
        }
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff > 0;
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return Shifts.schoolTimes();
    }
}
