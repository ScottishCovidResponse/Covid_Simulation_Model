package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.Person;

import java.util.ArrayList;

public class Shop extends CommunalPlace {
    public Shop(int cindex) {
        super(cindex);
        this.transProb = super.transProb * 5d / (5000d / 200d);
        this.startDay = 1;
        this.endDay = 7;
        this.keyProb = 0.5;
        if (Math.random() > this.keyProb) this.keyPremises = true;

    }

    public void shoppingTrip(ArrayList<Person> vHouse) {
        this.listPeople.addAll(vHouse);
    }

    public ArrayList<Person> sendHome(int hour) {
        ArrayList<Person> vReturn = new ArrayList<>();
        for (int i = 0; i < this.listPeople.size(); i++) {
            Person nPers = this.listPeople.get(i);
            if (!nPers.isShopWorker() && Math.random() < 0.5 || hour < super.endTime) {// Assumes a median lenght of shopping trip of 2 hours
                vReturn.add(nPers);
                this.listPeople.remove(i);
                i--;
            }
        }
        return vReturn;
    }
}
