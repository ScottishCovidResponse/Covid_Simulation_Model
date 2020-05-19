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
import java.util.List;

public abstract class CommunalPlace {

    private static final Logger LOGGER = LogManager.getLogger(CommunalPlace.class);

    abstract public void reportInfection(DailyStats s);

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

    public CommunalPlace() {
        this.rng = RNG.get();
        this.listPeople = new ArrayList<>();
        this.startTime = 8; // The hour of the day that the Communal Place starts
        this.endTime = 17; // The hour of the day that it ends
        this.startDay = 1; // Days of the week that it is active - start
        this.endDay = 5; // Days of the week that it is active - end
        this.transProb = PopulationParameters.get().getpBaseTrans(); // Pretty important parameter. This defines the transmission rate within this Communal Place
        this.keyProb = 1.0;
        this.sDistance = 1.0;
        if (rng.nextUniform(0, 1) > this.keyProb) this.keyPremises = true;

    }

    public void overrideKeyPremises(boolean overR) {
        this.keyPremises = overR;
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

    protected void doInfect(DailyStats stats) {
        List<Person> deaths = new ArrayList<>();
        for (Person cPers : listPeople) {
            if (cPers.getInfectionStatus() && !cPers.isRecovered()) {
                cPers.stepInfection();
                if (cPers.isInfectious()) {
                    for (Person nPers : listPeople) {
                        if (cPers != nPers) {
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
                    deaths.add(cPers);
                }
                if (cPers.cStatus() == CStatus.RECOVERED) {
                    cPers.setRecovered(true);
                }
            }
        }
        listPeople.removeAll(deaths);
    }

    // Cycle through the People objects in the Place and test their infection status etc
    public void cyclePlace(int time, DailyStats stats) {
        doInfect(stats);

        List<Person> left = new ArrayList<>();
        for (Person cPers : listPeople) {
            if (time == endTime) {
                cPers.returnHome();
                left.add(cPers);
            }
        }

        listPeople.removeAll(left);
    }

    public void adjustSDist(double sVal) {
        this.sDistance = sVal;
    }

}
