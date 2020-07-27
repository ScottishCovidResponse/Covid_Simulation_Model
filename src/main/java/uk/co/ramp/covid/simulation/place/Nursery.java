package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;

import java.util.List;

public class Nursery extends CommunalPlace {

    public Nursery(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.nurseryExpectedInteractionsPerHour;
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
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.schoolTimes;
    }
}
