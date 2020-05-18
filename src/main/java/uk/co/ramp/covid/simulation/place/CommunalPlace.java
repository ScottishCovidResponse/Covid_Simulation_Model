/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.RunModel;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Pensioner;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.PopulationParameters;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;

public abstract class CommunalPlace {

    private static final Logger LOGGER = LogManager.getLogger(CommunalPlace.class);

    abstract public void reportInfection(DailyStats s);

    private int cIndex;
    protected int startTime;
    protected int endTime;
    protected ArrayList<Person> listPeople;
    protected int startDay;
    protected int endDay;
    protected double transProb;
    protected boolean keyPremises;
    protected double keyProb;
    private double sDistance; // A social distancing coefficient;
    protected final RandomDataGenerator rng;

    public CommunalPlace(int cIndex) {
        this.rng = RNG.get();
        this.listPeople = new ArrayList<>();
        this.startTime = 8; // The hour of the day that the Communal Place starts
        this.endTime = 17; // The hour of the day that it ends
        this.startDay = 1; // Days of the week that it is active - start
        this.endDay = 5; // Days of the week that it is active - end
        this.cIndex = cIndex; // This sets the index for each Communal Place to avoid searching
        this.transProb = PopulationParameters.get().getpBaseTrans(); // Pretty important parameter. This defines the transmission rate within this Communal Place
        this.keyProb = 1.0;
        this.sDistance = 1.0;
        if (rng.nextUniform(0, 1) > this.keyProb) this.keyPremises = true;

    }

    public void overrideKeyPremises(boolean overR) {
        this.keyPremises = overR;
    }

    public int getIndex() {
        return this.cIndex;
    }

    public void setIndex(int indexVal) {
        this.cIndex = indexVal;
    }

    // Check whether a Person might visit at that hour of the day
    public boolean checkVisit(Person cPers, int time, int day, boolean clockdown) {
        boolean cIn = false;
        if (this.startTime == time && day >= this.startDay && day <= this.endDay && (this.keyPremises || !clockdown)) {
            cIn = true;
            this.listPeople.add(cPers);
        }
        return cIn;
    }
    private void registerInfection(DailyStats s, Person p) {
        reportInfection(s);
        p.reportInfection(s);
    }

    // Cycle through the People objects in the Place and test their infection status etc
    public ArrayList<Person> cyclePlace(int time, DailyStats stats) {
        ArrayList<Person> cReturn = new  ArrayList<>();
        CStatus status = null;
        for (int i = 0; i < this.listPeople.size(); i++) {
            Person cPers = this.listPeople.get(i);
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                status = cPers.stepInfection();
                if (cPers.cStatus() == CStatus.ASYMPTOMATIC || cPers.cStatus() == CStatus.PHASE1 || cPers.cStatus() == CStatus.PHASE2) {
                    for (int k = 0; k < this.listPeople.size(); k++) {
                        if (k != i) {
                            Person nPers = this.listPeople.get(k);
                            if (!nPers.getInfectionStatus()) {
                                boolean infected = nPers.infChallenge(this.transProb * this.sDistance);
                                if (infected) {
                                    registerInfection(stats, nPers);
                                }
                            }
                        }
                    }
                }
                if (cPers.cStatus() == CStatus.DEAD) {
                    cPers.reportDeath(stats);
                    this.listPeople.remove(i);
                    i--;
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.setRecovered(true);
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
