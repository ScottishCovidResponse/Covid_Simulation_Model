package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.*;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;

import java.util.List;

public class Restaurant extends CommunalPlace {

    public Restaurant(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.restaurantExpectedInteractionsPerHour;
    }

    @Override
    protected void setKey() {
        keyPremises = false;
    }

    @Override
    protected List<BuildingTimeParameters> getTimeInfo() {
        return PopulationParameters.get().buildingProperties.restaurantTimes;
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.restaurantInfectionsWorker.increment();
        } else {
            s.restaurantInfectionsVisitor.increment();
        }
    }

    @Override
    public void determineMovement(Time t, DailyStats s, Places places) {
        movePhase2(t, s, places);
        moveShifts(t);
        moveVisitors(t, PopulationParameters.get().buildingProperties.pLeaveRestaurantHour);
    }

}
