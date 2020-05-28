package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class Office extends CommunalPlace {

    public Office() {
        this(Size.UNKNOWN);
    }


    public Office(Size s)  {
        super(s);
        transAdjustment = PopulationParameters.get().getpOfficeTrans();
        keyProb = PopulationParameters.get().getpOfficeKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionOffice();
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return Shifts.nineFiveFiveDays();
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff > 0;
    }
}
