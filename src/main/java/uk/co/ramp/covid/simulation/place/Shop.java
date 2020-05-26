package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.util.ArrayList;

public class Shop extends CommunalPlace {

    public Shop() {
        this(Size.UNKNOWN);
    }

    public Shop(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() *  PopulationParameters.get().getpShopTrans();
        keyProb = PopulationParameters.get().getpShopKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;

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
            // TODO: Average shopping time should be a parameter
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
    public void doMovement(int day, int hour) {
        moveShifts(day, hour);
        sendHome(day, hour);
    }
}
