package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class Office extends CommunalPlace {

    public Office(Size s)  {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.officeExpectedInteractionsPerHour;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    protected void setKey() {
        keyPremises = PopulationParameters.get().buildingProperties.pOfficeKey.sample();
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.officeInfectionsWorker.increment();
        } else {
            s.officeInfectionsVisitor.increment();
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
