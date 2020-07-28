package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;

import java.util.List;

public class Shop extends CommunalPlace {

    public Shop(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.shopExpectedInteractionsPerHour;

    }

    @Override
    protected void setKey() {
        keyPremises = PopulationParameters.get().buildingProperties.pShopKey.sample();
    }


    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.shopInfectionsWorker.increment();
        } else {
            s.shopInfectionsVisitor.increment();
        }
    }

    @Override
    public void determineMovement(Time t, DailyStats s, Places places) {
        movePhase2(t, s, places);
        moveShifts(t);
        moveVisitors(t, PopulationParameters.get().buildingProperties.pLeaveShopHour);
    }

    @Override
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.shopTimes;
    }
}
