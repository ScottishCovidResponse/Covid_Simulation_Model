package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

public class Nursery extends CommunalPlace {

    public Nursery(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.nurseryExpectedInteractionsPerHour;
        times = OpeningTimes.nineFiveWeekdays();
    }

    @Override
    protected void setKey() {
        keyPremises = false;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t) && p.getAge() >= 18) {
            s.nurseryInfectionsWorker.increment();
        } else {
            s.nurseryInfectionsVisitor.increment();
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
