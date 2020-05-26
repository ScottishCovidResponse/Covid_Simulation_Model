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
        times.setOpen(10);
        times.setClose(22);
        keyPremises = false;
    }

    public void shoppingTrip(ArrayList<Person> vHouse) {
        people.addAll(vHouse);
    }

    public int sendHome(int day, int hour) {
        ArrayList<Person> left = new ArrayList<>();
        for (Person nPers : people) {
            // TODO: average dining time should be a parameter
            if (!nPers.isShopWorker() && rng.nextUniform(0, 1) < 0.4
                    || !times.isOpen(hour + 1, day)) {
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

    // TODO: Specialised handling for restaurants
    @Override
    public void doMovement(int day, int hour) {
        moveShifts(day, hour);
        //sendHome(day, hour);
    }

}
