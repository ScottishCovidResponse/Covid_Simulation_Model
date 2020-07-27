package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.*;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;

import java.util.List;

public class Restaurant extends CommunalPlace {

    private ShiftAllocator shifts;

    public Restaurant(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.restaurantExpectedInteractionsPerHour;
        setOpeningHours();
    }

    @Override
    protected void setKey() {
        keyPremises = false;
    }

    private void setOpeningHours() {
        ProbabilityDistribution<BuildingTimeParameters> dist = new ProbabilityDistribution<>();
        for (BuildingTimeParameters t : PopulationParameters.get().buildingProperties.restaurantTimes) {
            if (t.probability != null) {
                dist.add(t.probability, t);
            }
        }

        BuildingTimeParameters timing = dist.sample();
        times = timing.openingTime;
        shifts = new ShiftAllocator(timing.shifts);
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return shifts.getNext();
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff >= 4;
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
