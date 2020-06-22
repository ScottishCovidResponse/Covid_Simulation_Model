package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RNG;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

public class Restaurant extends CommunalPlace {

    private RoundRobinAllocator<Shifts> shifts;

    public Restaurant(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.restaurantTransmissionConstant;
        setOpeningHours();
    }

    @Override
    protected void setKey() {
        keyPremises = false;
    }

    private void setOpeningHours() {
        shifts = new RoundRobinAllocator<>();
        if (RNG.get().nextUniform(0, 1) < 0.5) {
            times = OpeningTimes.eightTenAllWeek();
            shifts.put(new Shifts(8, 15, 0, 1, 2));
            shifts.put(new Shifts(15, 22, 0, 1, 2));
            shifts.put(new Shifts(8, 15, 3, 4, 5, 6));
            shifts.put(new Shifts(15, 22, 3, 4, 5, 6));
        } else {
            times = OpeningTimes.tenTenAllWeek();
            shifts.put(new Shifts(10, 16, 0, 1, 2));
            shifts.put(new Shifts(16, 22, 0, 1, 2));
            shifts.put(new Shifts(10, 16, 3, 4, 5, 6));
            shifts.put(new Shifts(16, 22, 3, 4, 5, 6));
        }
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
            s.incInfectionsRestaurantWorker();
        } else {
            s.incInfectionsRestaurantVisitor();
        }
    }

    @Override
    public void determineMovement(Time t, DailyStats s, boolean lockdown, Places places) {
        movePhase2(t, s, places);
        moveShifts(t, lockdown);
        moveVisitors(t, PopulationParameters.get().buildingProperties.pLeaveRestaurant);
    }

}
