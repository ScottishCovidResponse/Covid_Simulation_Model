/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Pensioner;
import uk.co.ramp.covid.simulation.population.Person;

import java.util.ArrayList;

public class CommunalPlace {

    public int cindex;
    protected int startTime;
    protected int endTime;
    protected ArrayList<Person> listPeople;
    protected int startDay;
    protected int endDay;
    protected double transProb;
    protected boolean keyPremises;
    protected double keyProb;
    private double sDistance; // A social distancing coefficient;

    public CommunalPlace(int cindex) {
        this.listPeople = new ArrayList<>();
        this.startTime = 8; // The hour of the day that the Communal Place starts
        this.endTime = 17; // The hour of the day that it ends
        this.startDay = 1; // Days of the week that it is active - start
        this.endDay = 5; // Days of the week that it is active - end
        this.cindex = cindex; // This sets the index for each Communal Place to avoid searching
        this.transProb = 0.45; // Pretty important parameter. This defines the transmission rate within this Communal Place
        this.keyProb = 1.0;
        this.sDistance = 1.0;
        if (Math.random() > this.keyProb) this.keyPremises = true;

    }

    public void overrideKeyPremises(boolean overR) {
        this.keyPremises = overR;
    }

    public int getIndex() {
        return this.cindex;
    }

    public void setIndex(int indexVal) {
        this.cindex = indexVal;
    }

    // Check whether a Person might visit at that hour of the day
    public boolean checkVisit(Person cPers, int time, int day, boolean clockdown) {
        boolean cIn = false;
        if (this.startTime == time && day >= this.startDay && day <= this.endDay && (this.keyPremises || !clockdown)) {
            cIn = true;
            this.listPeople.add(cPers);
            if (cPers instanceof Pensioner && (this instanceof Hospital))
                System.out.println("Pensioner HERE " + cPers.getMIndex());
        }
        return cIn;
    }

    // Cyctek through the People objects in the Place and test their infection status etc
    public ArrayList<Person> cyclePlace(int time, int day) {

        ArrayList<Person> cReturn = new  ArrayList<>();
        CStatus status = null;
//	if(this instanceof School)	System.out.println(this.toString() + " Capacity = " + this.vPeople.size() + " " + this.keyPremises + this.transProb);
        for (int i = 0; i < this.listPeople.size(); i++) {
            Person cPers = this.listPeople.get(i);
            if (cPers.getInfectionStatus() && !cPers.recovered) {
                status = cPers.stepInfection();
                if (cPers.cStatus() == CStatus.ASYMPTOMATIC || cPers.cStatus() == CStatus.PHASE1 || cPers.cStatus() == CStatus.PHASE2) {
                    for (int k = 0; k < this.listPeople.size(); k++) {
                        if (k != i) {
                            Person nPers = this.listPeople.get(k);
                            if (!nPers.getInfectionStatus()) {
                                //System.out.println("Trans prob = "+this.transProb);
                                nPers.infChallenge(this.transProb * this.sDistance);
                                //	if(this instanceof Shop) System.out.println(this.toString() + "   " + nPers.shopWorker + " " + this.transProb);
                            }
                        }
                    }
                }
                if (cPers.cStatus() == CStatus.DEAD) {
                    this.listPeople.remove(i);
                    //	System.out.println("Work Dead");  // Printing key metrics of infection to check that the model is working
                    i--;
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.recovered = true;
                    //	System.out.println("Recovered");  // Printing key metrics of infection to check that the model is working
                }
            }
            if (time == this.endTime && status != CStatus.DEAD) {
                cReturn.add(cPers);
                this.listPeople.remove(i);
                i--;
            }
        }
        return cReturn;
    }

    public void adjustSDist(double sVal) {
        this.sDistance = sVal;
    }

}
