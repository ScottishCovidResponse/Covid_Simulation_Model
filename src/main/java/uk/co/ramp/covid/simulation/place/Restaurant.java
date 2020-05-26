package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.util.ArrayList;

public class Restaurant extends CommunalPlace {

    public Restaurant() {
        this(Size.UNKNOWN);
    }
    
    public Restaurant(Size s) {
        super(s);
        transProb = PopulationParameters.get().getpBaseTrans() * PopulationParameters.get().getpRestaurantTrans();
        startDay = 1;
        endDay = 7;
        startTime = 10;
        endTime = 22;
        keyPremises = false;
    }

    public void shoppingTrip(ArrayList<Person> vHouse) {
        people.addAll(vHouse);
    }

    public int sendHome(int hour) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            if (!nPers.isShopWorker() && rng.nextUniform(0, 1) < 0.4
                    || hour < super.endTime) { // Assumes a median length of shopping trip of 2 hours
                left.add(nPers);
                nPers.returnHome();
            }
        }
        people.removeAll(left);
        return left.size();
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsRestaurant();
    }

}
