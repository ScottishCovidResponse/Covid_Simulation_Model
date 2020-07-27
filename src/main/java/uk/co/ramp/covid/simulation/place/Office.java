package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;

import java.util.List;

public class Office extends CommunalPlace {

    public Office(Size s)  {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.officeExpectedInteractionsPerHour;
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
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.officeTimes;
    }
}
