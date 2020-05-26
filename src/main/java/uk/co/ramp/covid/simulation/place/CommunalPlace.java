/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;
import java.util.List;

public abstract class CommunalPlace extends Place {

    private static final Logger LOGGER = LogManager.getLogger(CommunalPlace.class);

    public enum Size {
        SMALL, MED, LARGE, UNKNOWN;
    }
    
    protected Size size;
    protected OpeningTimes times;
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
        this.times = new OpeningTimes(8,17,1,5, OpeningTimes.getAllDays());

        this.keyProb = 1.0;
        if (rng.nextUniform(0, 1) > this.keyProb) this.keyPremises = true;
    }

    public void overrideKeyPremises(boolean overR) {
        this.keyPremises = overR;
    }

    // Check whether a Person might visit at that hour of the day
    public boolean checkVisit(Person cPers, int time, int day, boolean clockdown) {
        boolean cIn = false;
        if (times.isOpen(time, day) && (this.keyPremises || !clockdown)) {
            cIn = true;
            people.add(cPers);
        }
        return cIn;
    }

    // Cycle through the People objects in the Place and test their infection status etc
    public void cyclePlace(int time, int day, DailyStats stats) {
        doInfect(stats);

        List<Person> left = new ArrayList<>();
        for (Person cPers : people) {
            if (!times.isOpen(time, day)) {
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

    /** Move everyone based on their shift patterns */
    public void moveShifts(int day, int hour) {
        List<Person> left = new ArrayList();
        for (Person p : people) {
            if (!p.worksNextHour(this, day, hour)) {
                p.returnHome();
                left.add(p);
            }
        }
        people.removeAll(left);
    }
    
    public boolean isVisitorOpenNextHour(int day, int hour) {
        return hour + 1 >= times.getVisitorOpen() 
                && hour + 1 < times.getVisitorClose();
    }

    @Override
    public void doMovement(int day, int hour) {
        moveShifts(day, hour);
    }
}
