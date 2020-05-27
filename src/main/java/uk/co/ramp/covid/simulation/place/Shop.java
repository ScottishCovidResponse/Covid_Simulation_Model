package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RoundRobinAllocator;

import java.util.ArrayList;

public class Shop extends CommunalPlace {
    
    private RoundRobinAllocator<Shifts> shifts;

    public Shop() {
        this(Size.UNKNOWN);
    }

    public Shop(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() *  PopulationParameters.get().getpShopTrans();
        keyProb = PopulationParameters.get().getpShopKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;
        setOpeningHours();
    }
    
    private void setOpeningHours() {
        shifts = new RoundRobinAllocator();
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
        return shifts.getNext();
    }

    public void shoppingTrip(ArrayList<Person> vHouse) {
       people.addAll(vHouse);
    }

    public int sendHome(int day, int hour) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (nPers.mustGoHome(day, hour)) {
                left.add(nPers);
                nPers.returnHome();
            }
            else if (rng.nextUniform(0, 1) < PopulationParameters.get().getpLeaveShop()
                    || !times.isOpen(hour + 1, day)) {
                nPers.returnHome();
                left.add(nPers);
            }
        }
        people.removeAll(left);
        return left.size();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsShop();
    }

    @Override
    public void doMovement(int day, int hour, boolean lockdown) {
        moveShifts(day, hour, lockdown);
        sendHome(day, hour);
    }
}
