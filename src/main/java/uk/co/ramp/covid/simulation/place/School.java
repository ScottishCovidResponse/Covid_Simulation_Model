package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class School extends CommunalPlace {

    public School(Size s) {
        super(s);
        times = OpeningTimes.nineFiveWeekdays();
        transAdjustment = PopulationParameters.get().buildingProperties.pSchoolTrans;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t) && p.getAge() >= 18) {
            s.incInfectionsSchoolWorker();
        } else {
            s.incInfectionsSchoolVisitor();
        }
    }


    @Override
    public Shifts getShifts() {
        nStaff++;
        return Shifts.schoolTimes();
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff > 0;
    }

}
