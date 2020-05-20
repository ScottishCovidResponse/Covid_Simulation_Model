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

public abstract class CommunalPlace extends Place {

    private static final Logger LOGGER = LogManager.getLogger(CommunalPlace.class);

    public enum Size {
        SMALL, MED, LARGE, UNKNOWN;
    }
    
    protected Size size;

    protected int startTime;
    protected int endTime;
    protected int startDay;
    protected int endDay;
    protected boolean keyPremises;
    protected double keyProb;

    protected final RandomDataGenerator rng;

    public CommunalPlace(Size s) {
        this();
        size = s;
    }

    public CommunalPlace() {
        super();
        this.rng = RNG.get();
        this.startTime = 8; // The hour of the day that the Communal Place starts
        this.endTime = 17; // The hour of the day that it ends
        this.startDay = 1; // Days of the week that it is active - start
        this.endDay = 5; // Days of the week that it is active - end
        this.keyProb = 1.0;
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
            people.add(cPers);
        }
        return cIn;
    }

    // Cycle through the People objects in the Place and test their infection status etc
    public void cyclePlace(int time, DailyStats stats) {
        doInfect(stats);

        List<Person> left = new ArrayList<>();
        for (Person cPers : people) {
            if (time == endTime) {
                cPers.returnHome();
                left.add(cPers);
            }
        }

        people.removeAll(left);
    }

    public void adjustSDist(double sVal) {
        this.sDistance = sVal;
    }

    public Size getSize() {
        return size;
    }
    public void setSize(Size s) {
        size = s;
    }

}
