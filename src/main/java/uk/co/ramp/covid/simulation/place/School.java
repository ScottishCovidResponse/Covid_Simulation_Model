package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

public class School extends CommunalPlace {
    public School() {
        this(Size.UNKNOWN);
    }

    public School(Size s) {
        super(s);
        times.setOpen(9);
        times.setClose(15);
        times.setOpenDays(OpeningTimes.getWeekdays());
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpSchoolTrans();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsSchool();
    }

    @Override
    public void doMovement(int day, int hour) {
        moveShifts(day, hour);
    }
}
