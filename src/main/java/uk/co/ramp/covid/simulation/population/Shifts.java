package uk.co.ramp.covid.simulation.population;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import uk.co.ramp.covid.simulation.place.OpeningTimes;

import java.util.HashMap;
import java.util.Map;

/** Class to track the shifts people are at their primary places (i.e. nursery visit times are also shifts) */
public class Shifts {

    public static class Shift {
        private final int start;
        private final int end;

        public Shift(int s, int e) {
            start = s;
            end = e;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    private final Map<Integer, Shift> shifts;

    public Shifts() {
        shifts = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            shifts.put(i, new Shift(0,0));
        }
    }

    public Shifts(int s, int e, int... days) {
        this();
        for (int day : days) {
            shifts.put(day, new Shift(s, e));
        }
    }

    public Shift getShift(int day) {
        return shifts.get(day);
    }

    // Common shift patterns
    private static Shifts nineFiveFiveDays;
    public static Shifts nineFiveFiveDays() {
        if (nineFiveFiveDays == null) {
            nineFiveFiveDays = new Shifts(9, 17, 0, 1, 2, 3, 4);
        }
        return nineFiveFiveDays;
    }

    private static Shifts schoolTimes;
    public static Shifts schoolTimes() {
        if (schoolTimes == null) {
            schoolTimes = new Shifts(9,17, 0, 1, 2, 3, 4);

        }
        return schoolTimes;
    }

    public static JsonDeserializer<Shifts> deserializer = (json, typeOfT, context) -> {
        JsonObject o = json.getAsJsonObject();
        int start = o.get("start").getAsInt();
        int end = o.get("end").getAsInt();
        JsonArray daysA = o.get("days").getAsJsonArray();
        int[] days = new Gson().fromJson(daysA, int[].class);

        return new Shifts(start, end, days);
    };

}
