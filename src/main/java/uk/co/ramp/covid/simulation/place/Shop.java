package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;

import java.util.ArrayList;

public class Shop extends CommunalPlace {
    public Shop(int cindex) {
        super(cindex);
        transProb = PopulationParameters.get().getpBaseTrans() *  PopulationParameters.get().getpShopTrans();
        startDay = 1;
        endDay = 7;
        keyProb = PopulationParameters.get().getpShopKey();
        if (rng.nextUniform(0, 1) > keyProb) keyPremises = true;

    }

    public void shoppingTrip(ArrayList<Person> vHouse) {
        this.listPeople.addAll(vHouse);
    }

    public ArrayList<Person> sendHome(int hour) {
        ArrayList<Person> vReturn = new ArrayList<>();
        for (int i = 0; i < this.listPeople.size(); i++) {
            Person nPers = this.listPeople.get(i);
            if (!nPers.isShopWorker() && rng.nextUniform(0, 1) < 0.5 || hour < super.endTime) {// Assumes a median lenght of shopping trip of 2 hours
                vReturn.add(nPers);
                this.listPeople.remove(i);
                i--;
            }
        }
        return vReturn;
    }

    @Override
    public void reportInfection(DailyStats s) {
        s.incInfectionsShop();
    }
}
