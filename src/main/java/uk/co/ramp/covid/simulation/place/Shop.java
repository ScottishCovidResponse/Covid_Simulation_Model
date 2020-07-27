package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.parameters.BuildingTimeParameters;
import uk.co.ramp.covid.simulation.util.ProbabilityDistribution;
import uk.co.ramp.covid.simulation.util.ShiftAllocator;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class Shop extends CommunalPlace {
    
    private ShiftAllocator shifts;

    public Shop(Size s) {
        super(s);
        expectedInteractionsPerHour = PopulationParameters.get().buildingProperties.shopExpectedInteractionsPerHour;
        setOpeningHours();
    }

    @Override
    protected void setKey() {
        keyPremises = PopulationParameters.get().buildingProperties.pShopKey.sample();
    }

    private void setOpeningHours() {
        for (BuildingTimeParameters t : PopulationParameters.get().buildingProperties.shopTimes) {
            if (size == t.sizeCondition) {
                times = t.openingTime;
                shifts = new ShiftAllocator(t.shifts);
            }
        }
    }

    @Override
    public Shifts getShifts() {
        nStaff++;
        return shifts.getNext();
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
    public boolean isFullyStaffed() {
        if (size == Size.SMALL)
            return nStaff >= 2;
        else {
            return nStaff >= 4;
        }
    }
}
