package uk.co.ramp.covid.simulation;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

public class DateRange {
    final Time start;
    final Time end;
    
    public DateRange(Time start, Time end) {
        this.start = start;
        this.end = end;
    }
    
    public boolean inRange(Time t) {
        return start.compareTo(t) <= 0 && end.compareTo(t) > 0;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }

    public static JsonSerializer<DateRange> serializer = (src, typeOfSrc, context) -> {
        JsonObject o = new JsonObject();
        o.addProperty("start", src.getStart().getAbsDay());
        o.addProperty("end", src.getEnd().getAbsDay());
        return o;
    };

    public static JsonDeserializer<DateRange> deserializer = (json, typeOfT, context) -> {
        JsonObject o = json.getAsJsonObject();
        int s = o.get("start").getAsInt();
        int e = o.get("end").getAsInt();
        return new DateRange(Time.timeFromDay(s), Time.timeFromDay(e));
    };
}
