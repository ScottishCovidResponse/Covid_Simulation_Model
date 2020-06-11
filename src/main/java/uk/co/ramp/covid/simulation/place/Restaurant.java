package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.parameters.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RNG;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;

public class Restaurant extends CommunalPlace {

    private RoundRobinAllocator<Shifts> shifts;

    public Restaurant(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.restaurantTransmissionConstant;
        keyPremises = false;
        setOpeningHours();
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

    public void shoppingTrip(ArrayList<Person> vHouse) {
        people.addAll(vHouse);
    }

    public int sendHome(Time t) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            // People may have already left if their family has
            if (left.contains(nPers)) {
                continue;
            }

            if (nPers.worksNextHour(this, t, false)) {
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (nPers.mustGoHome(t)) {
                left.add(nPers);
                nPers.returnHome();
                left.addAll(sendFamilyHome(nPers, this, t));
            }
            else if (PopulationParameters.get().buildingProperties.pLeaveRestaurant.sample()
                    || !times.isOpenNextHour(t)) {
                left.add(nPers);
                nPers.returnHome();
                left.addAll(sendFamilyHome(nPers, this, t));
            }
        }
        people.removeAll(left);
        return left.size();
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
    public void doMovement(Time t, boolean lockdown, Places places) {
        movePhase2(t, places);
        moveShifts(t, lockdown);
        sendHome(t);
    }

}
