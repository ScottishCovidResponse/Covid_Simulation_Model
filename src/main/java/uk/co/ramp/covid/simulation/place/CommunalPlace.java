/*
 * Code for managing the Communal Places where People objects mix
 */

package uk.co.ramp.covid.simulation.place;

import org.apache.commons.math3.random.RandomDataGenerator;
import uk.co.ramp.covid.simulation.output.DailyStats;
import uk.co.ramp.covid.simulation.util.Time;
import uk.co.ramp.covid.simulation.util.DateRange;
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
    private boolean closed = false;

    protected int nStaff = 0;

    protected final RandomDataGenerator rng;
    
    public abstract Shifts getShifts();
    protected List<DateRange> holidays;
    
    public CommunalPlace(Size s) {
        this();
        size = s;
    }

    public CommunalPlace() {
        super();
        this.rng = RNG.get();
        this.times = new OpeningTimes(8,17,1,5, OpeningTimes.getAllDays());
        setHolidays();
        setKey();
    }
    
    protected abstract void setKey();

    public void overrideKeyPremises(boolean key) {
        keyPremises = key;
        if (keyPremises && closed) {
            closed = false;
        }
    }

    public Size getSize() {
        return size;
    }
    public void setSize(Size s) {
        size = s;
    }

    /** Move everyone based on their shift patterns */
    // Everyone who has been hospitalised (and their families) will have left at this point
    public void moveShifts(Time t, Function<Person, Boolean> filter) {
        for (Person p : getPeople() ) {
            if (p.hasMoved() || filter.apply(p) || !p.isWorking(this, t)) {
                continue;
            }

            if (p.worksNextHour(this, t)) {
                p.stayInPlace(this);
            } else {
                p.returnHome(this);
            }
        }
    }
    
    public void moveShifts(Time t) {
        moveShifts(t, p -> false);
    }

    /** Moves Phase2 people to either hospital or back home */
    public void movePhase2(Time t, DailyStats s, Places places, Function<Person,Boolean> filter) {
        for (Person p : getPeople()) {
            if (p.hasMoved() || filter.apply(p)) {
                continue;
            }

            if (p.cStatus() != null && p.cStatus() == CStatus.PHASE2) {
                if (p.goesToHosptialInPhase2()) {
                    CovidHospital h = places.getRandomCovidHospital();
                    p.hospitalise(s);
                    p.moveTo(this, h);
                } else {
                    p.returnHome(this);
                }

                if (!p.isWorking(this, t)) {
                    sendFamilyHome(p, this, t);
                }

            }
        }
    }


    public void moveVisitors(Time t, Probability pLeave) {
        for (Person p : getPeople()) {
            // People may have already left if their family has
            if (p.hasMoved() || p.isWorking(this, t)) {
                continue;
            }

            // Under certain conditions we must go home, e.g. if there is a shift starting soon
            if (p.mustGoHome(t)) {
                p.returnHome(this);
                sendFamilyHome(p, this, t);
                continue;
            }

            if (pLeave.sample() || !isOpenNextHour(t)) {
                p.returnHome(this);
                sendFamilyHome(p, this, t);
            } else {
                p.stayInPlace(this);
                keepFamilyInPlace(p, t);
            }
        }
    }


    public void movePhase2(Time t, DailyStats s, Places places) {
        movePhase2(t, s, places, p -> false);
    }
    
    public boolean isVisitorOpenNextHour(Time t) {
        return isOpenNextHour(t) && times.isOpenToVisitors(t.advance());
    }
    
    public boolean isOpenNextHour(Time t) {
        return isOpen(t.advance());
    }

    public boolean isOpen(Time t) {
        if (closed) {
            return false;
        }

        for (DateRange holiday : holidays) {
            if (holiday.inRange(t)) {
                return false;
            }
        }

        return times.isOpen(t);
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
    public void determineMovement(Time t, DailyStats s, Places places) {
        movePhase2(t, s, places);
        moveShifts(t);
    }

    public boolean isKeyPremises() {
        return keyPremises;
    }

    public int getnStaff() {
        return nStaff;
    }
    
    public abstract boolean isFullyStaffed();

    public OpeningTimes getTimes() { return times; }
    
    public void setHolidays() {
        holidays = new ArrayList<>();
    }
    
    public void enterLockdown(double sDist) {
        sDistance = sDist;
        if (!keyPremises) {
            closed = true;
        }
    }
    
    public void exitLockdown() {
        sDistance = 1.0;
        closed = false;
    }
    
    public void close() {
        closed = true;
    }
    
    public void open() {
        closed = false;
    }
    
    public boolean isClosed() {
        return closed;
    }
}
