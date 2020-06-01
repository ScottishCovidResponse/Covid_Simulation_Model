package uk.co.ramp.covid.simulation;

import java.util.Objects;

/** Time tracks the current simulation time */
public class Time {

    /** Time in hours since the start of the simulation */
    private int absTime;
    /** Current day */
    private int day;
    /** Current hour within day */
    private int hour;

    public Time(int startingHour) {
        this.absTime = startingHour;
        this.day = (startingHour / 24) % 7;
        this.hour = startingHour % 24;
    }

    public Time() {
        this(0);
    }

    public int getAbsTime() {
        return absTime;
    }
    
    public int getAbsDay() { return absTime / 24; }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public Time advance() {
        return new Time(absTime + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return absTime == time.absTime;

    }

    @Override
    public int hashCode() {
        return Objects.hash(absTime);
    }
}
