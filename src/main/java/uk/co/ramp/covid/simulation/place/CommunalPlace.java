/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;
import java.util.List;

public abstract class CommunalPlace extends Place {

    public enum Size {
        SMALL, MED, LARGE
    }
    
    protected Size size;
    protected OpeningTimes times;
    protected boolean keyPremises;
    protected Probability keyProb;

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
        this.keyPremises = false;
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
    public void moveShifts(Time t, boolean lockdown) {
        List<Person> left = new ArrayList<>();
        for (Person p : people) {
            if (!p.worksNextHour(this, t, lockdown)) {
                p.returnHome();
                left.add(p);
            }
        }
        people.removeAll(left);
    }
    
    public boolean isVisitorOpenNextHour(Time t) {
        return  times.getOpenDays().get(t.getDay())
                && t.getHour() + 1 >= times.getVisitorOpen()
                && t.getHour() + 1 < times.getVisitorClose();
    }

    public boolean isOpen(int day, int hour) {
        if (!times.getOpenDays().get(day)) {
            return false;
        }

        return hour >= times.getOpen()
                && hour < times.getClose();
    }

    public List<Person> getStaff(Time t) {
        List<Person> res = new ArrayList<>();
        for (Person p : people) {
            if (p.isWorking(this, t)) {
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public void doMovement(Time t, boolean lockdown) {
        moveShifts(t, lockdown);
    }

    public boolean isKeyPremises() {
        return keyPremises;
    }

    public int getnStaff() {
        return nStaff;
    }
    
    public abstract boolean isFullyStaffed();

    public OpeningTimes getTimes() { return times; }
}
