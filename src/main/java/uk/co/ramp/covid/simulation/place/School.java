package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

import java.util.ArrayList;
import java.util.List;

public class School extends CommunalPlace {

    public School(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.schoolExpectedInteractionsPerHour;
    }

    @Override
    protected void setKey() {
        keyPremises = false;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t) && p.getAge() >= 18) {
            s.schoolInfectionsWorker.increment();
        } else {
            s.schoolInfectionsVisitor.increment();
        }
    }

    @Override
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.schoolTimes;
    }

    @Override
    public void setHolidays() {
        holidays = new ArrayList<>();
        holidays.addAll(PopulationParameters.get().buildingProperties.schoolHolidays);
    }

}
