/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.Time;
import uk.co.ramp.covid.simulation.covid.Covid;
import uk.co.ramp.covid.simulation.population.CStatus;
import uk.co.ramp.covid.simulation.population.Person;
import uk.co.ramp.covid.simulation.population.Places;
import uk.co.ramp.covid.simulation.population.Shifts;
import uk.co.ramp.covid.simulation.util.Probability;
import uk.co.ramp.covid.simulation.util.RNG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public void moveShifts(Time t, boolean lockdown, Function<Person, Boolean> filter) {
        List<Person> left = new ArrayList<>();
        for (Person p : getPeople()) {
            if (filter.apply(p)) {
                continue;
            }

            if (!p.worksNextHour(this, t, lockdown)) {
                left.add(p);
            }
        }
        left.forEach(p -> p.returnHome(this));
    }
    
    public void moveShifts(Time t, boolean lockdown) {
        moveShifts(t, lockdown, p -> false);
    }

    /** Moves Phase2 people to either hospital or back home */
    public void movePhase2(Time t, Places places, Function<Person,Boolean> filter) {
        List<Person> left = new ArrayList<>();
        List<Runnable> hospitalMoves = new ArrayList<>();

        for (Person p : getPeople()) {
            if (filter.apply(p)) {
                continue;
            }

            if (p.cStatus() != null && p.cStatus() == CStatus.PHASE2) {
                if (p.goesToHosptialInPhase2()) {
                    CovidHospital h = places.getRandomCovidHospital();
                    p.hospitalise();
                    hospitalMoves.add(() -> p.moveTo(this, h));
                } else {
                    left.add(p);
                    left.addAll(getFamilyToSendHome(p, this, t));
                }
            }
        }
        left.forEach(p -> p.returnHome(this));
        hospitalMoves.forEach(m -> m.run());
    }

    public void movePhase2(Time t, Places places) {
        movePhase2(t, places, p -> false);
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
        for (Person p : getPeople()) {
            if (p.isWorking(this, t)) {
                res.add(p);
            }
        }
        return res;
    }

    @Override
    public void determineMovement(Time t, boolean lockdown, Places places) {
        movePhase2(t, places);
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
