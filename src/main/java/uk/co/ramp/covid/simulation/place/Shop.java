package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.Person;

import java.util.Vector;

public class Shop extends CommunalPlace {
    public Shop(int cindex) {
        super(cindex);
        this.transProb = super.transProb * 5 / (5000 / 200);
        this.startDay = 1;
        this.endDay = 7;
        this.keyProb = 0.5;
        if (Math.random() > this.keyProb) this.keyPremises = true;

    }

    public void shoppingTrip(Vector vHouse) {
        for (int i = 0; i < vHouse.size(); i++) {
            this.vPeople.addElement((Person) vHouse.elementAt(i));
        }
    }

    public Vector sendHome(int hour) {
        Vector vReturn = new Vector();
        for (int i = 0; i < this.vPeople.size(); i++) {
            Person nPers = (Person) this.vPeople.elementAt(i);
            if (!nPers.shopWorker && Math.random() < 0.5 || hour < super.endTime) {// Assumes a median lenght of shopping trip of 2 hours
                vReturn.addElement(nPers);
                this.vPeople.removeElementAt(i);
                i--;
            }
        }
        return vReturn;
    }
}
