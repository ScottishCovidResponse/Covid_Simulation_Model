package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;

public class Shop extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

    public Shop(Size s) {
        super(s);
        transAdjustment = PopulationParameters.get().buildingProperties.pShopTrans;
        keyProb = PopulationParameters.get().buildingProperties.pShopKey;
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
        setOpeningHours();
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
            else if (rng.nextUniform(0, 1) < PopulationParameters.get().buildingProperties.pLeaveShop
                    || !times.isOpenNextHour(t)) {
                nPers.returnHome();
                left.add(nPers);
                left.addAll(sendFamilyHome(nPers, this, t));
            }
        }
        people.removeAll(left);
        return left.size();
    }

    @Override
    public void reportInfection(Time t, Person p, DailyStats s) {
        if (p.isWorking(this, t)) {
            s.incInfectionsShopWorker();
        } else {
            s.incInfectionsShopVisitor();
        }
    }

    @Override
    public void doMovement(Time t, boolean lockdown) {
        moveShifts(t, lockdown);
        sendHome(t);
    }

    @Override
    public boolean isFullyStaffed() {
        return nStaff >= 4;
    }
}
