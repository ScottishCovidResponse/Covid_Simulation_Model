/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.ramp.covid.simulation.DailyStats;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;
import java.util.List;

public abstract class CommunalPlace extends Place {

    public enum Size {
        SMALL, MED, LARGE, UNKNOWN
    }
    
    protected Size size;
    protected OpeningTimes times;
    protected boolean keyPremises;
    protected double keyProb;

    protected int nStaff = 0;

    protected final RandomDataGenerator rng;
    
    public abstract Shifts getShifts();

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
    public void moveShifts(int day, int hour, boolean lockdown) {
        List<Person> left = new ArrayList();
        for (Person p : people) {
            if (!p.worksNextHour(this, day, hour, lockdown)) {
                p.returnHome();
                left.add(p);
            }
        }
        people.removeAll(left);
    }
    
    public boolean isVisitorOpenNextHour(int day, int hour) {
        return  times.getOpenDays().get(day)
                && hour + 1 >= times.getVisitorOpen()
                && hour + 1 < times.getVisitorClose();
    }

    public boolean isOpen(int day, int hour) {
        if (!times.getOpenDays().get(day)) {
            return false;
        }

        return hour >= times.getOpen()
                && hour < times.getClose();
    }

    public List<Person> getStaff(int day, int hour) {
        List<Person> res = new ArrayList<>();
        for (Person p : people) {
            if (p.isWorking(this, day, hour)) {
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public void doMovement(int day, int hour, boolean lockdown) {
        moveShifts(day, hour, lockdown);
    }
    
    public boolean isKeyPremises() {
        return keyPremises;
    }

    public int getnStaff() {
        return nStaff;
    }
    
    public abstract boolean isFullyStaffed();
}
