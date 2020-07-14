package uk.co.ramp.covid.simulation.util;

import com.google.gson.*;
import java.util.Objects;

/** Time tracks the current simulation time */
public class Time implements Comparable<Time> {

    /** Time in hours since the start of the simulation */
    private final int absTime;
    /** Current day */
    private final int day;
    /** Current hour within day */
    private final int hour;

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
    
    public static Time timeFromDay(int absDay) {
        return new Time(absDay*24);
    }

    @Override
    public int compareTo(Time t) {
        return Integer.compare(getAbsTime(), t.getAbsTime());
    }

    public static JsonDeserializer<Time> deserializer = (json, typeOfT, context) -> {
        int day = json.getAsInt();
        return Time.timeFromDay(day);
    };

    public static JsonSerializer<Time> serializer = (src, typeOfSrc, context) -> {
        return new JsonPrimitive(src.getAbsDay());
    };
}
