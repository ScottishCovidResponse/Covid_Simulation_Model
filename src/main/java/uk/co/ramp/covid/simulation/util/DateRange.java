package uk.co.ramp.covid.simulation.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import java.util.Objects;

public class DateRange {
    final Time startDay;
    final Time endDay;
    
    public DateRange(Time start, Time end) {
        this.startDay = start;
        this.endDay = end;
    }
    
    public boolean inRange(Time t) {
        return startDay.compareTo(t) <= 0 && endDay.compareTo(t) > 0;
    }

    public Time getStartDay() {
        return startDay;
    }

    public Time getEndDay() {
        return endDay;
    }

    public static JsonSerializer<DateRange> serializer = (src, typeOfSrc, context) -> {
        JsonObject o = new JsonObject();
        o.addProperty("startDay", src.getStartDay().getAbsDay());
        o.addProperty("endDay", src.getEndDay().getAbsDay());
        return o;
    };

    public static JsonDeserializer<DateRange> deserializer = (json, typeOfT, context) -> {
        JsonObject o = json.getAsJsonObject();
        int s = o.get("startDay").getAsInt();
        int e = o.get("endDay").getAsInt();
        return new DateRange(Time.timeFromDay(s), Time.timeFromDay(e));
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(startDay, dateRange.startDay) &&
                Objects.equals(endDay, dateRange.endDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDay, endDay);
    }
}
