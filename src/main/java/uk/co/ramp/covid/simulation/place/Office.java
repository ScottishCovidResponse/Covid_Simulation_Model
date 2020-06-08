package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class Office extends CommunalPlace {

    public Office(Size s)  {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.pOfficeTrans;
        keyProb = PopulationParameters.get().buildingProperties.pOfficeKey;
        if (keyProb.sample()) keyPremises = true;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.incInfectionOfficeWorker();
        } else {
            s.incInfectionsOfficeVisitor();
        }
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
