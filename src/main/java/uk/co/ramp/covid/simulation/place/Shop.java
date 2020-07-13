package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class Shop extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

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
        shifts = new RoundRobinAllocator<>();
        if (size == Size.SMALL) {
            times = OpeningTimes.nineFiveAllWeek();
            shifts.put(new Shifts(9,17, 0, 1, 2));
            shifts.put(new Shifts(9,17, 3, 4, 5, 6));
        } else {
            times = OpeningTimes.eightTenAllWeek();
            shifts.put(new Shifts(8,15, 0, 1, 2));
            shifts.put(new Shifts(15,22, 0, 1, 2));
            shifts.put(new Shifts(8,15, 3, 4, 5, 6));
            shifts.put(new Shifts(15,22, 3, 4, 5, 6));
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
        moveVisitors(t, PopulationParameters.get().buildingProperties.pLeaveShop);
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
