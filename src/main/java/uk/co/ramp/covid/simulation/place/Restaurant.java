package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RNG;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;

public class Restaurant extends CommunalPlace {

    private RoundRobinAllocator<Shifts> shifts;

    public Restaurant() {
        this(Size.UNKNOWN);
    }
    
    public Restaurant(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().getpRestaurantTrans();
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

    public int sendHome(int day, int hour) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            // People may have already left if their family has
            if (left.contains(nPers)) {
                continue;
            }

            if (nPers.worksNextHour(this, day, hour, false)) {
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (nPers.mustGoHome(day, hour)) {
                left.add(nPers);
                nPers.returnHome();
                left.addAll(sendFamilyHome(nPers, this, day, hour));
            }
            else if (rng.nextUniform(0, 1) < PopulationParameters.get().getpLeaveRestaurant()
                    || !times.isOpen(hour + 1, day)) {
                left.add(nPers);
                nPers.returnHome();
                left.addAll(sendFamilyHome(nPers, this, day, hour));
            }
        }
        people.removeAll(left);
        return left.size();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsRestaurant();
    }

    @Override
    public void doMovement(int day, int hour, boolean lockdown) {
        moveShifts(day, hour, lockdown);
        sendHome(day, hour);
    }

}
