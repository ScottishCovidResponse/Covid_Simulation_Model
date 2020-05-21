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
        startDay = 1;
        endDay = 7;
        keyProb = PopulationParameters.get().getpShopKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;

    }

    public void shoppingTrip(ArrayList<Person> vHouse) {
       people.addAll(vHouse);
    }

    public int sendHome(int hour) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            if (!nPers.isShopWorker() && rng.nextUniform(0, 1) < 0.5 
                    || hour < super.endTime) {// Assumes a median lenght of shopping trip of 2 hours
                left.add(nPers);
                nPers.returnHome();
            }
        }
        people.removeAll(left);
        return left.size();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsShop();
    }
}
