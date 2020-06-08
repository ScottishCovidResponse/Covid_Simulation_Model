package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class Nursery extends CommunalPlace {

    public Nursery(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.pNurseryTrans;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t) && p.getAge() >= 18) {
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
